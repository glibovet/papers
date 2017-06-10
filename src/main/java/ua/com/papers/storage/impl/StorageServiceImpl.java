package ua.com.papers.storage.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publications.IPublicationValidateService;
import ua.com.papers.storage.IStorage;
import ua.com.papers.storage.IStorageService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Stream;

/**
 * Created by Andrii on 05.10.2016.
 */
@Service
public class StorageServiceImpl implements IStorageService {

    @Autowired
    private IPublicationService publicationService;
    @Autowired
    private IStorage storage;
    @Autowired
    private IPublicationValidateService publicationValidateService;

    @Override
    public boolean uploadPaper(int id, String url) throws NoSuchEntityException, StorageException {
        publicationService.getPublicationById(id);
        File papersContainer = new File(ROOT_DIR + PUBLICATIONS_FOLDER + '/' + id);
        if(!papersContainer.exists())
            papersContainer.mkdirs();

        String fileName = id + "." + FilenameUtils.getExtension(url);
        final File serverFile = new File(papersContainer.getAbsolutePath() + '/' + fileName);

        InputStream input = null;
        OutputStream output = null;
        try {
            input = new URL(url).openStream();
            output = new FileOutputStream(serverFile);

            IOUtils.copy(input, output);
        } catch (IOException e) {
            try {
                  papersContainer.delete();
            } catch (Exception e1) { }
            throw new StorageException(e);
        } finally {
            close(input);
            close(output);
        }

        if (useRemote) {
            async(() -> {
                try {
                    storage.upload(Files.readAllBytes(serverFile.toPath()), fileName, PUBLICATIONS_FOLDER + '/' + id);
                } catch (StorageException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return true;
    }

    @Override
    @Transactional
    public boolean uploadPaper(int id, MultipartFile file) throws NoSuchEntityException, ServiceErrorException, IOException, ValidationException {
        PublicationEntity publication = publicationService.getPublicationById(id);
        File papersContainer = new File(ROOT_DIR + PUBLICATIONS_FOLDER + '/' + id);
        if(!papersContainer.exists())
            papersContainer.mkdirs();

        String fileName = id + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        final File serverFile = new File(papersContainer.getAbsolutePath() + '/' + fileName);
        copyFile(file, serverFile);

        if (useRemote) {
            async(() -> {
                try {
                    storage.upload(Files.readAllBytes(serverFile.toPath()), fileName, PUBLICATIONS_FOLDER + '/' + id);
                } catch (StorageException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        publication.setFileNameOriginal(file.getOriginalFilename());
        publicationService.updatePublication(publication);

        return true;
    }

    @Override
    public byte[] getPaperAsByteArray(Integer paperId) throws NoSuchEntityException, ServiceErrorException, ForbiddenException {
        PublicationEntity entity = publicationService.getPublicationById(paperId);
        return getPaperAsByteArray(entity);
    }

    public byte[] getPaperAsByteArray(PublicationEntity publication) throws ServiceErrorException, ForbiddenException, NoSuchEntityException {
        if (publication == null)
            throw new ServiceErrorException();
        if (!publicationValidateService.isPublicationAvailable(publication))
            throw new ForbiddenException();

        File publicationFolder = new File(ROOT_DIR + PUBLICATIONS_FOLDER + '/' + publication.getId());
        if (!publicationFolder.exists())
            throw new NoSuchEntityException("publication", "id: " + publication.getId());

        File publicationFile = fileByPartName(publicationFolder, publication.getId() + "");
        if (publicationFile == null) {
            throw new NoSuchEntityException("publication", "no file for publication with id: " + publication.getId());
        }

        try {
            return Files.readAllBytes(publicationFile.toPath());
        } catch (IOException e) {
            throw new ServiceErrorException();
        }
    }

    @Override
    @Transactional
    public void getPaper(int id, HttpServletResponse response) throws NoSuchEntityException, ForbiddenException, ServiceErrorException {
        PublicationEntity publication = publicationService.getPublicationById(id);
        if (!publicationValidateService.isPublicationAvailable(publication))
            throw new ForbiddenException();

        try {
            File publicationFolder = new File(ROOT_DIR + PUBLICATIONS_FOLDER + '/' + publication.getId());
            if (!publicationFolder.exists())
                throw new NoSuchEntityException("publication", "id: " + publication.getId());

            File publicationFile = fileByPartName(publicationFolder, publication.getId() + "");
            if (publicationFile == null) {
                throw new NoSuchEntityException("publication", "no file for publication with id: " + publication.getId());
            }

            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment; filename="+publication.getFileNameOriginal());
            response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");

            response.getOutputStream().write(Files.readAllBytes(publicationFile.toPath()));
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    @Transactional
    public boolean paperHasFile(int id) throws NoSuchEntityException {
        File publicationFolder = new File(ROOT_DIR + PUBLICATIONS_FOLDER + '/' + id);
        if (!publicationFolder.exists())
            throw new NoSuchEntityException("publication", "id: " + id);

        return fileByPartName(publicationFolder, id + "") != null;
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

    private void async(Runnable runnable) {
        new Thread(runnable).start();
    }

    private File fileByPartName(File folder, String name) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && name.equals(fileName(f))) {
                    return f;
                }
            }
        }

        return null;
    }

    private String fileName(File file) {
        int dot = file.getName().lastIndexOf('.');
        if (dot > -1) {
            return file.getName().substring(0, dot);
        }

        return file.getName();
    }

    private void close(Closeable stream) {
        try {
            stream.close();
        } catch (Exception e) { }
    }

    private final String ROOT_DIR = System.getProperty("catalina.home") + "/papers";

    private final String PUBLICATIONS_FOLDER = "/publications";

    @Value("${remote_storage.use}")
    private boolean useRemote;
}
