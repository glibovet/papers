package ua.com.papers.storage;

import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.pojo.storage.FileItem;

import java.util.List;

/**
 * Created by oleh_kurpiak on 01.10.2016.
 */
public interface IStorage {

    /**
     *
     * @param file - file in bytes that should be saved
     * @param fileName - save passed file with name
     * @param folder - save to folder. pass null to save file to root dir
     */
    void upload(byte[] file, String fileName, String folder) throws StorageException;

    /**
     * delete file from storage folder by full name
     *
     * @param fileName - full file name(with extension)
     * @param folder - directory where file is located. or null if in root directory
     */
    void delete(String fileName, String folder) throws StorageException;

    /**
     * list of folders and files in #folder
     * pass NULL to get list from root dir
     *
     * @param folder - folder where to list all children
     * @return - list of file and folders
     */
    List<FileItem> listFiles(String folder) throws StorageException;
}
