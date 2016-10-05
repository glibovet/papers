package ua.com.papers.storage.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.storage.IStorage;
import ua.com.papers.storage.IStorageService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Andrii on 05.10.2016.
 */
@Service
public class IStorageServiceImpl implements IStorageService {

    @Autowired
    IPublicationService publicationService;
    @Autowired
    IStorage storage;

    @Override
    public boolean uploadPaper(int id, MultipartFile file) throws NoSuchEntityException, ServiceErrorException, IOException, ValidationException {
        PublicationEntity publication = publicationService.getPublicationById(id);
        String rootDir = System.getProperty("catalina.home")+tomcatLocalFolder;
        File papersContainer = new File(rootDir + '/' + PAPERS_CONTEINER);
        if(!papersContainer.exists())
            papersContainer.mkdirs();
        String fileName = id+"."+FilenameUtils.getExtension(file.getOriginalFilename());
        File serverFile = new File(rootDir+File.separator+ PAPERS_CONTEINER+ File.separator + fileName);
        copyFile(file,serverFile);
        storage.upload(Files.readAllBytes(serverFile.toPath()),fileName,papersFolder);
        publication.setFileNameOriginal(file.getOriginalFilename());
        publicationService.updatePublication(publication);
        serverFile.delete();
        return true;
    }

    private void copyFile(MultipartFile source, File result) throws IOException, ServiceErrorException {
        if(source == null || source.getBytes() == null || source.getBytes().length == 0 || result == null)
            throw new ServiceErrorException();
        FileOutputStream fileOutputStream = new FileOutputStream(result);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(source.getBytes());
        bufferedOutputStream.close();
        fileOutputStream.close();
    }

    @Value("${dropbox.papers.folder}")
    private String papersFolder;

    @Value("${tomcat.papers.folder}")
    private String tomcatLocalFolder;

    @Value("${papers.container")
    private String PAPERS_CONTEINER;
}
