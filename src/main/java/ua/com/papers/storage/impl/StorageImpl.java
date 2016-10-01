package ua.com.papers.storage.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.pojo.storage.FileItem;
import ua.com.papers.pojo.storage.ItemType;
import ua.com.papers.storage.IStorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
            client().files().uploadBuilder(fullPath(fileName, folder))
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

    private String fullPath(String name, String folder){
        if(folder == null || folder.isEmpty() || folder.compareTo("/") == 0){
            if(name.charAt(0) == '/')
                return name;
            return '/' + name;
        } else {
            if(name.charAt(0) == '/')
                return folder + name;
            return folder + '/' + name;
        }
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
