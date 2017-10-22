package ua.com.papers.storage.impl;


import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.async.LaunchEmptyResult;
import com.dropbox.core.v2.files.*;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.pojo.storage.FileData;
import ua.com.papers.pojo.storage.FileItem;
import ua.com.papers.pojo.storage.ItemType;
import ua.com.papers.storage.IStorage;
import ua.com.papers.utils.ResultCallback;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Created by oleh_kurpiak on 01.10.2016.
 */
@Service
@Log
public class StorageImpl implements IStorage {
    /**
     * Actually, documentation says uploading up to 1000 files
     * in batch is allowed
     * <a href="https://www.dropbox.com/developers/reference/data-ingress-guide">see link</a>
     */
    private static final int MAX_BATCH_SIZE = 10;
    /**
     * Max time to wait between insertions into upload queue
     */
    private static final long MAX_IDLE_AWAIT_TIMEOUT = 30 * 1000L;

    private static final class UploadJob {
        private final List<UploadSessionFinishArg> uploadArgs;
        private final Map<File, ResultCallback<File>> callbacks;
        private long lastInsertTimestamp;
        private LaunchEmptyResult batchUploadResult;
        private final DbxClientV2 clientV2;

        private UploadJob(DbxClientV2 clientV2) {
            this.lastInsertTimestamp = 0L;
            this.uploadArgs = new ArrayList<>();
            this.callbacks = new HashMap<>();
            this.clientV2 = clientV2;
        }

        void appendFile(File src, File to, ResultCallback<File> callback) throws DbxException, IOException {

            try (val inputStream = new FileInputStream(src)) {
                // start upload session
                val sessionId = clientV2.files().uploadSessionStart(true).uploadAndFinish(inputStream).getSessionId();
                val cursor = new UploadSessionCursor(sessionId, src.length());


                val shouldUpload = shouldUpload();

                clientV2.files().uploadSessionAppendV2(cursor, shouldUpload);

                val uploadArg = new UploadSessionFinishArg(cursor, new CommitInfo(to.getPath().replaceAll("\\\\", "/"), WriteMode.OVERWRITE, true, new Date(System.currentTimeMillis()), true));

                uploadArgs.add(uploadArg);
                callbacks.put(src, callback);
                lastInsertTimestamp = System.currentTimeMillis();
            }
        }

        long getLastInsertTimestamp() {
            return lastInsertTimestamp;
        }

        int getBatchSize() {
            return uploadArgs.size();
        }

        boolean isUploading() {
            return batchUploadResult != null && !batchUploadResult.isComplete();
        }

        private void upload(Runnable end) {

            try {
                batchUploadResult = clientV2.files().uploadSessionFinishBatch(uploadArgs);

                log.log(Level.INFO, String.format("starting batch upload, thread %s, job %s, files size %d",
                        Thread.currentThread(), batchUploadResult.getAsyncJobIdValue(), uploadArgs.size()));
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
            } catch (final DbxException e) {
                log.log(Level.SEVERE, "db exception occurred", e);
                notifyException(e);
            } finally {
                if (end != null) {
                    end.run();
                }
            }
        }

        private boolean shouldUpload() {
            val currentTime = System.currentTimeMillis();
            return !isUploading() && (uploadArgs.size() >= StorageImpl.MAX_BATCH_SIZE
                    || currentTime - lastInsertTimestamp >= StorageImpl.MAX_IDLE_AWAIT_TIMEOUT);
        }

        private void notifySuccess() {
            for (val entry : callbacks.entrySet()) {
                entry.getValue().onResult(entry.getKey());
            }
        }

        private void notifyException(Exception e) {
            for (val callback : callbacks.values()) {
                callback.onException(e);
            }
        }
    }

    private final ExecutorService executorService;
    private final Queue<UploadJob> uploadJobs;

    public StorageImpl() {
        this.uploadJobs = new LinkedList<>();
        this.executorService = Executors.newWorkStealingPool();
    }

    @Override
    public void upload(@NotNull File from, @NotNull File to, @NotNull ResultCallback<File> callback) {
        executorService.submit(() -> {
            try {
                doUpload(from, to, callback);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void upload(byte[] file, String fileName, String folder) throws StorageException {
        InputStream inputStream = new ByteArrayInputStream(file);
        try {
            String path = fullPath(fileName, folder);
            try {
                Metadata metadata = client().files().getMetadata(path);
                if (metadata != null && metadata.getName() != null) {
                    client().files().delete(path);
                }
            } catch (GetMetadataErrorException e) {
                // ignore
            }

            client().files().uploadBuilder(path)
                    .uploadAndFinish(inputStream);
        } catch (DbxException e) {
            throw new StorageException(e);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void delete(String fileName, String folder) throws StorageException {
        try {
            String fullName = fullPath(fileName, folder);
            client().files().delete(fullName);
        } catch (DbxException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public List<FileItem> listFiles(String folder) throws StorageException {
        if (folder == null || folder.compareTo("/") == 0)
            folder = "";
        else if (folder.charAt(0) != '/')
            folder = '/' + folder;

        try {
            List<FileItem> files = new ArrayList<>();
            ListFolderResult result = client().files().listFolder(folder);
            for (Metadata metadata : result.getEntries()) {
                ItemType type;
                if (metadata instanceof FileMetadata) {
                    type = ItemType.FILE;
                } else if (metadata instanceof FolderMetadata) {
                    type = ItemType.FOLDER;
                } else {
                    continue;
                }

                files.add(new FileItem(metadata.getName(), type, metadata.getPathDisplay()));
            }

            return files;
        } catch (ListFolderErrorException e) {
            throw new StorageException(e);
        } catch (DbxException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public FileData download(OutputStream stream, String partOfName, String folder) throws StorageException {
        try {
            List<FileItem> files = listFiles(folder);
            for (FileItem item : files) {
                if (item.type == ItemType.FILE) {
                    int dot = item.name.indexOf('.');
                    dot = dot > -1 ? dot : item.name.length();
                    String name = item.name.substring(0, dot);
                    if (partOfName.compareTo(name) == 0) {
                        DownloadBuilder builder = client().files().downloadBuilder(item.path);
                        FileMetadata data = builder.start().download(stream);

                        return new FileData(item.name, data.getSize());
                    }
                }
            }
            throw new StorageException(new NullPointerException(String.format("file[%s] was not founded", partOfName)));
        } catch (IOException e) {
            throw new StorageException(e);
        } catch (DbxException e) {
            throw new StorageException(e);
        }
    }

    private void doUpload(File from, File to, ResultCallback<File> callback) {
        Preconditions.checkNotNullAll(from, to, callback);
        final UploadJob lastJob;

        synchronized (uploadJobs) {
            var job = uploadJobs.peek();
            var isNeedNewJob = job == null;

            if (!isNeedNewJob) {
                synchronized (job) {
                    isNeedNewJob = job.isUploading();
                }
            }

            if (isNeedNewJob) {
                job = new UploadJob(client());
                uploadJobs.add(job);
                log.log(Level.INFO, String.format("inserting a new upload job, queue size %d", uploadJobs.size()));
            }
            lastJob = job;
        }

        synchronized (lastJob) {
            val shouldUpload = StorageImpl.shouldUpload(lastJob);

            if (shouldUpload) {
                log.log(Level.INFO, "Start upload");
                synchronized (uploadJobs) {
                    uploadJobs.remove(lastJob);
                }
                lastJob.upload(this::runNextJob);
            } else {
                try {
                    log.log(Level.INFO, String.format("Appending files %s, %s", from, to));
                    lastJob.appendFile(from, to, callback);
                } catch (final DbxException | IOException e) {
                    callback.onException(new StorageException(e));
                } finally {
                    lastJob.notifyAll();
                }

                try {
                    lastJob.wait(StorageImpl.MAX_IDLE_AWAIT_TIMEOUT);
                } catch (final InterruptedException e) {
                    log.log(Level.INFO, "Interrupted upload wait lock", e);
                }

                if (StorageImpl.shouldUpload(lastJob)) {
                    lastJob.upload(this::runNextJob);
                }
            }
        }
    }

    private void runNextJob() {
        synchronized (uploadJobs) {
            val job = uploadJobs.peek();

            if (job != null && StorageImpl.shouldUpload(job)) {
                uploadJobs.remove(job);
                job.upload(this::runNextJob);
            }
        }
    }

    private static boolean shouldUpload(UploadJob job) {
        return !job.isUploading() && job.getBatchSize() > 0 && (job.getBatchSize() >= StorageImpl.MAX_BATCH_SIZE
                || System.currentTimeMillis() - job.getLastInsertTimestamp() > StorageImpl.MAX_IDLE_AWAIT_TIMEOUT);
    }

    private String fullPath(String name, String folder) {
        String path;

        if (name.charAt(0) == '/') {
            path = name;
        } else {
            path = '/' + name;
        }

        if (folder != null && !folder.isEmpty() && folder.compareTo("/") != 0) {
            if (folder.charAt(0) == '/') {
                path = folder + path;
            } else {
                path = '/' + folder + path;
            }
        }

        return path;
    }

    private DbxClientV2 client() {
        var local = dbxClient;

        if (local == null) {
            synchronized (this) {
                local = dbxClient;

                if (local == null) {
                    val config = DbxRequestConfig.newBuilder(appName).withUserLocale("en_EN").build();
                    dbxClient = local = new DbxClientV2(config, token);
                }
            }
        }
        return local;
    }

    private volatile DbxClientV2 dbxClient;

    @Value("${dropbox.app_name}")
    private String appName;

    @Value("${dropbox.app_token}")
    private String token;
}
