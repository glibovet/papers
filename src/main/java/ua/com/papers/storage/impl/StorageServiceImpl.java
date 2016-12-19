package ua.com.papers.storage.impl;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.storage.FileData;
import ua.com.papers.pojo.storage.FileItem;
import ua.com.papers.pojo.storage.ItemType;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publications.IPublicationValidateService;
import ua.com.papers.storage.IStorage;
import ua.com.papers.storage.IStorageService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by Andrii on 05.10.2016.
 */
@Service
public class StorageServiceImpl implements IStorageService {

    @Autowired
    IPublicationService publicationService;
    @Autowired
    IStorage storage;
    @Autowired
    IPublicationValidateService publicationValidateService;

    @Override
    @Transactional
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

    @Override
    public byte[] getPaperAsByteArray(Integer paperId) throws NoSuchEntityException, ServiceErrorException, ForbiddenException {
        PublicationEntity entity = publicationService.getPublicationById(paperId);
        return getPaperAsByteArray(entity);
    }

    public byte[] getPaperAsByteArray(PublicationEntity publication) throws ServiceErrorException, ForbiddenException {
        if (publication == null)
            throw new ServiceErrorException();
        if (!publicationValidateService.isPublicationAvailable(publication))
            throw new ForbiddenException();
        byte[] res = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileData fileData = storage.download(bos,String.valueOf(publication.getId()),papersFolder);
        if (fileData == null)
            throw new ServiceErrorException();
        return bos.toByteArray();
    }

    @Override
    @Transactional
    public void getPaper(int id, HttpServletResponse response) throws NoSuchEntityException, ForbiddenException, ServiceErrorException {
        byte[] bytes = getPaperAsByteArray(id);
        try {
            response.getOutputStream().write(bytes);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    @Transactional
    public boolean paperHasFile(int id) throws NoSuchEntityException, ForbiddenException, ServiceErrorException {
        List<FileItem> files = storage.listFiles(papersFolder);
        if (files != null) {
            for (FileItem item : files) {
                if (item.type == ItemType.FILE) {
                    String name = item.name;
                    int dot = name.indexOf('.');
                    if (dot > -1)
                        name = name.substring(0, dot);

                    if (name.equals(id + ""))
                        return true;
                }
            }
        }
        return false;
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

    @Value("${papers.container}")
    private String PAPERS_CONTEINER;
}
