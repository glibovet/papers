package ua.com.papers.storage.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.pojo.storage.FileData;
import ua.com.papers.pojo.storage.FileItem;
import ua.com.papers.pojo.storage.ItemType;
import ua.com.papers.storage.IStorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleh_kurpiak on 01.10.2016.
 */
@Service
public class StorageImpl implements IStorage {

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
        if(folder == null || folder.compareTo("/") == 0)
            folder = "";
        else if(folder.charAt(0) != '/')
            folder = '/' + folder;

        try {
            List<FileItem> files = new ArrayList<>();
            ListFolderResult result = client().files().listFolder(folder);
            for (Metadata metadata : result.getEntries()) {
                ItemType type;
                if(metadata instanceof FileMetadata){
                    type = ItemType.FILE;
                } else if(metadata instanceof FolderMetadata){
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
            for(FileItem item : files){
                if(item.type == ItemType.FILE){
                    int dot = item.name.indexOf('.');
                    dot = dot > -1 ? dot : item.name.length();
                    String name = item.name.substring(0, dot);
                    if(partOfName.compareTo(name) == 0){
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

    private String fullPath(String name, String folder){
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

    private DbxClientV2 client(){
        if(dbxClient == null) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).withUserLocale("en_EN").build();
            dbxClient = new DbxClientV2(config, token);
        }
        return dbxClient;
    }

    private DbxClientV2 dbxClient;

    @Value("${dropbox.app_name}")
    private String appName;

    @Value("${dropbox.app_token}")
    private String token;
}
