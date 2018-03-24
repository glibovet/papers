package ua.com.papers.storage.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.async.LaunchEmptyResult;
import com.dropbox.core.v2.files.CommitInfo;
import com.dropbox.core.v2.files.UploadSessionCursor;
import com.dropbox.core.v2.files.UploadSessionFinishArg;
import com.dropbox.core.v2.files.WriteMode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.utils.ResultCallback;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

@ToString
@Log
final class UploadJob {

    private static final AtomicLong ID_GEN = new AtomicLong(Long.MIN_VALUE);

    private final long id = ID_GEN.incrementAndGet();
    private long lastInsertTimestamp;
    private LaunchEmptyResult batchUploadResult;
    private final DbxClientV2 clientV2;
    private volatile boolean isUploading, isUploaded;

    private final List<UploadArgs> uploadArgs;

    @Value
    static final class UploadArgs {
        File src;
        File to;
        ResultCallback<File> callback;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            UploadArgs that = (UploadArgs) o;
            return Objects.equals(src, that.src) &&
                    Objects.equals(to, that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), src, to);
        }
    }

    UploadJob(DbxClientV2 clientV2, UploadArgs args) {
        this.lastInsertTimestamp = 0L;
        this.clientV2 = clientV2;
        this.uploadArgs = new ArrayList<>();

        append(args);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadJob uploadJob = (UploadJob) o;
        return id == uploadJob.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    void append(@NonNull UploadArgs args) {
        if (uploadArgs.stream().anyMatch(a -> a.src.equals(args.src))) {
            log.log(Level.INFO, "no new args were supplied, skipping");
            return;
        }

        uploadArgs.add(args);
        lastInsertTimestamp = System.currentTimeMillis();
        log.log(Level.INFO, String.format("new args were supplied, %s", args));
    }

    long getLastInsertTimestamp() {
        return lastInsertTimestamp;
    }

    int getBatchSize() {
        return uploadArgs.size();
    }

    boolean isUploading() {
        return isUploading;
    }

    boolean isUploaded() {
        return isUploaded;
    }

    void upload(@Nullable Runnable end) {
        Preconditions.checkArgument(!isUploaded, "Create a new job instead");
        log.log(Level.INFO, String.format("Starting upload %s", this));
        isUploading = true;

        try {
            doUpload();
        } catch (final Exception e) {
            log.log(Level.SEVERE, "db exception occurred", e);
            notifyException(e);
        } finally {
            batchUploadResult = null;
            isUploading = false;
            isUploaded = true;

            if (end != null) {
                end.run();
            }
        }
    }

    private void doUpload() throws IOException, DbxException {
        val uploadSessionArgs = prepareUploadSession();

        batchUploadResult = clientV2.files().uploadSessionFinishBatch(uploadSessionArgs);

        log.log(Level.INFO, String.format("starting batch upload, thread %s, job %s, files size %d",
                Thread.currentThread(), batchUploadResult.getAsyncJobIdValue(), uploadSessionArgs.size()));

        var isCompleted = false;

        while (!isCompleted) {

            try {
                val checkResult = clientV2.files().uploadSessionFinishBatchCheck(batchUploadResult.getAsyncJobIdValue());

                isCompleted = checkResult.isComplete();

                log.log(Level.INFO, String.format("job %s is uploading, status %s",
                        batchUploadResult.getAsyncJobIdValue(), checkResult.toStringMultiline()));

                Thread.sleep(1000L);
            } catch (final InterruptedException e) {
                log.log(Level.INFO, String.format("thread %s was interrupted", Thread.currentThread()), e);
            }
        }

        log.log(Level.INFO, String.format("job %s has completed", batchUploadResult.getAsyncJobIdValue()));
        notifySuccess();
    }

    private List<UploadSessionFinishArg> prepareUploadSession() throws DbxException, IOException {
        val uploadSessionArgs = new ArrayList<UploadSessionFinishArg>();

        for (var i = 0; i < uploadArgs.size(); ++i) {
            val arg = uploadArgs.get(i);

            try (val inputStream = new FileInputStream(arg.src)) {
                // start upload session
                val sessionId = clientV2.files().uploadSessionStart(true).uploadAndFinish(inputStream).getSessionId();
                val cursor = new UploadSessionCursor(sessionId, arg.src.length());

                clientV2.files().uploadSessionAppendV2(cursor, i + 1 == uploadArgs.size());

                val uploadArg = new UploadSessionFinishArg(cursor, newCommitInfo(arg.to));

                uploadSessionArgs.add(uploadArg);
            }
        }

        return uploadSessionArgs;
    }

    private CommitInfo newCommitInfo(File from) {
        return new CommitInfo(from.getPath().replaceAll("\\\\", "/"), WriteMode.OVERWRITE, true, new Date(System.currentTimeMillis()), true);
    }

    private void notifySuccess() {
        uploadArgs.forEach(args -> args.callback.onResult(args.src));
    }

    private void notifyException(Exception e) {
        uploadArgs.forEach(args -> args.callback.onException(new StorageException(e)));
    }
}
