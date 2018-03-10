package ua.com.papers.storage.impl;


import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.pojo.storage.FileData;
import ua.com.papers.pojo.storage.FileItem;
import ua.com.papers.pojo.storage.ItemType;
import ua.com.papers.storage.IStorage;
import ua.com.papers.utils.ResultCallback;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Created by oleh_kurpiak on 01.10.2016.
 */
@Service
@Log
public class StorageImpl implements IStorage {


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
                postJob(prepareJob(from, to, callback));
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

    private void postJob(@NonNull UploadJob job) {
        synchronized (job) {
            if (shouldUpload(job)) {
                synchronized (uploadJobs) {
                    val isRemoved = uploadJobs.remove(job);

                    if (!isRemoved) {
                        log.log(Level.WARNING, String.format("failed to remove job %s from queue of size %d", job, uploadJobs.size()));
                    }
                }

                job.upload(this::runNextJob);
                job.notifyAll();
            } else {
                // run timer
                try {
                    job.wait(maxIdleAwaitTimeout);
                } catch (final InterruptedException e) {
                    log.log(Level.INFO, "Interrupted upload wait lock", e);
                    return;
                }

                if (shouldUpload(job)) {
                    job.upload(this::runNextJob);
                    job.notifyAll();
                } else {
                    runNextJob();
                }
            }
        }
    }

    private UploadJob prepareJob(@NonNull File from, @NonNull File to, @NonNull ResultCallback<File> callback) {
        val args = new UploadJob.UploadArgs(from, to, callback);

        synchronized (uploadJobs) {
            var job = uploadJobs.peek();

            if (job == null || job.isUploading()) {
                job = new UploadJob(client(), args);

                uploadJobs.add(job);
                log.log(Level.INFO, String.format("inserting a new job %s, queue size %d", job, uploadJobs.size()));
            } else {
                job.append(args);
            }

            return job;
        }
    }

    private void runNextJob() {
        UploadJob job;

        synchronized (uploadJobs) {
            boolean drain;

            do {
                job = uploadJobs.peek();
                drain = job != null && (job.isUploaded() || job.isUploading());

                if (drain) {
                    uploadJobs.poll();
                }
            } while (drain);
        }

        if (job == null) {
            log.log(Level.INFO, "Empty upload queue");
        } else {
            log.log(Level.INFO, String.format("Posting next job %s", job));
            postJob(job);
        }
    }

    private boolean shouldUpload(UploadJob job) {
        return !job.isUploading() && !job.isUploaded() && (System.currentTimeMillis() - job.getLastInsertTimestamp() >= maxIdleAwaitTimeout
                || job.getBatchSize() >= maxBatchSize);
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

    /**
     * Actually, documentation says uploading up to 1000 files
     * in batch is allowed
     * <a href="https://www.dropbox.com/developers/reference/data-ingress-guide">see link</a>
     */
    @Value("${dropbox.max.batch.size}")
    private int maxBatchSize;

    /**
     * Max time to wait between insertions into upload queue
     */
    @Value("${dropbox.max.idle.timeout}")
    private long maxIdleAwaitTimeout;

}
