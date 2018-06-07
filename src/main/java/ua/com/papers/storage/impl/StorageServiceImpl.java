package ua.com.papers.storage.impl;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.crawler.core.main.util.UrlUtils;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publications.IPublicationValidateService;
import ua.com.papers.services.users.IChatService;
import ua.com.papers.services.users.IUserService;
import ua.com.papers.storage.IStorage;
import ua.com.papers.storage.IStorageService;
import ua.com.papers.utils.ResultCallback;
import ua.com.papers.utils.SecureToken;
import ua.com.papers.utils.TokenUtil;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Created by Andrii on 05.10.2016.
 */
@Service
public class StorageServiceImpl implements IStorageService {

    @Autowired
    private IPublicationService publicationService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IStorage storage;
    @Autowired
    private IPublicationValidateService publicationValidateService;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private IChatService chatService;

    @Override
    @SneakyThrows(MalformedURLException.class)
    public void uploadPaper(@NotNull PublicationEntity publication, @NotNull ResultCallback<File> callback) {
        Preconditions.checkNotNullAll(publication, callback, publication.getId(), publication.getFileLink());

        val papersContainer = new File(ROOT_DIR + PUBLICATIONS_FOLDER + '/' + publication.getId());

        if (!papersContainer.exists()) {
            papersContainer.mkdirs();
        }

        val contentType = UrlUtils.getContentType(new URL(publication.getFileLink()));
        val fileName = publication.getId() + "." + contentType.getExtension();
        val serverFile = new File(papersContainer, fileName);

        Optional<Exception> exception = Optional.empty();

        try (val input = new URL(publication.getFileLink()).openStream();
             val output = new FileOutputStream(serverFile)) {
            IOUtils.copy(input, output);
        } catch (final IOException e) {
            e.printStackTrace();

            //try {
            //   serverFile.delete();
            //} catch (final Exception e1) {
                exception = Optional.of(new StorageException(e));
            //}
        }

        if (exception.isPresent()) {
            callback.onException(exception.get());
        } else if (useRemote) {
            val storagePath = new File(StorageServiceImpl.fullPath(fileName, PUBLICATIONS_FOLDER + '/' + publication.getId()));

            storage.upload(serverFile, storagePath, new ResultCallback<File>() {
                @Override
                public void onResult(@NotNull File file) {
                    callback.onResult(file);
                }

                @Override
                public void onException(@NotNull Exception e) {
                    callback.onException(e);
                }
            });
        } else {
            callback.onResult(serverFile);
        }
    }

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
    @Transactional
    public boolean uploadProfileImage(UserEntity user, MultipartFile file) throws IOException, ServiceErrorException {
        if(file.isEmpty()) return false;
        putFileToServer(user.getId(), PROFILE_IMAGES_FOLDER, file);
        user.setPhoto(FilenameUtils.getName(file.getOriginalFilename()));
        userService.update(user);
        return true;
    }

    private void getAttachment (HttpServletResponse response, int entityId, String attachmentName, String folder) throws IOException {
        File file = new File(ROOT_DIR + folder +'/' + entityId + '/'+attachmentName);
        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }
        System.out.println("mimetype : "+mimeType);
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
        response.setContentLength((int)file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

    @Override
    public void getMessageAttachment(HttpServletResponse response, MessageEntity message) throws IOException {
        getAttachment(response, message.getId(), message.getAttachment(), MESSAGES_ATTACHMENTS_FOLDER);
    }

    @Override
    public void getContactAttachment(HttpServletResponse response, ContactEntity contact) throws IOException {
        getAttachment(response, contact.getId(), contact.getAttachment(), CONTACT_REQUESTS_ATTACHMENTS_FOLDER);
    }

    @Override
    @Transactional
    public boolean uploadRequestAttachment(ContactEntity contact, MultipartFile file) throws IOException, ServiceErrorException {
        putFileToServer(contact.getId(), CONTACT_REQUESTS_ATTACHMENTS_FOLDER, file);
        contact.setAttachment(FilenameUtils.getName(file.getOriginalFilename()));
        userService.update(contact);
        return true;
    }

    @Override
    @Transactional
    public boolean uploadMessageAttachment(MessageEntity message, MultipartFile file) throws IOException, ServiceErrorException {
        putFileToServer(message.getId(), MESSAGES_ATTACHMENTS_FOLDER, file);
        return true;
    }

    @Override
    @Transactional
    public boolean uploadMessageAttachment(MultipartFile file) throws IOException, ServiceErrorException {
        putFileToServer(-1, MESSAGES_ATTACHMENTS_FOLDER, file);
        return true;
    }

    private void putFileToServer(int entityId, String pathToFolder, MultipartFile file) throws IOException, ServiceErrorException {
        if(file.isEmpty()) return;
        File fileContainer = new File(ROOT_DIR + pathToFolder + '/' + entityId);
        if(!fileContainer.exists()) {
            fileContainer.mkdirs();
        }
        String fileName = FilenameUtils.getName(file.getOriginalFilename());
        final File serverFile = new File(fileContainer.getAbsolutePath() + '/' + fileName);
        copyFile(file, serverFile);
    }

    public void moveContactAttachmentToMessage(MessageEntity message, ContactEntity contact) throws IOException {
        String filePath = ROOT_DIR + CONTACT_REQUESTS_ATTACHMENTS_FOLDER + '/' + contact.getId() + '/' + contact.getAttachment();
        String toDirectoryPath = ROOT_DIR + MESSAGES_ATTACHMENTS_FOLDER + '/' + message.getId();
        moveAttachment(filePath,toDirectoryPath);
    }

    public void moveMessageAttachment (MessageEntity message) throws IOException {
        String filePath = ROOT_DIR + MESSAGES_ATTACHMENTS_FOLDER + "/-1/" + message.getAttachment();
        String toDirectoryPath = ROOT_DIR + MESSAGES_ATTACHMENTS_FOLDER + '/' + message.getId();
        moveAttachment(filePath,toDirectoryPath);
    }

    private void moveAttachment (String filePath, String toDirectoryPath) throws IOException {
        File from = new File(filePath);
        File fileContainer = new File(toDirectoryPath);
        if(!fileContainer.exists()) {
            fileContainer.mkdirs();
        }
        FileUtils.copyFileToDirectory(from, fileContainer);
        FileUtils.deleteDirectory(from.getParentFile());
    }

    @Override
    public byte[] getProfileImage (int userId) throws IOException {
        UserEntity user = null;
        try {
            user = userService.getUserById(userId);
        } catch (NoSuchEntityException e) {
            return getDefaultProfileImage();
        }
        final File serverFile = new File(ROOT_DIR + PROFILE_IMAGES_FOLDER +'/' + userId + '/'+user.getPhoto());
        if(!serverFile.exists()){
            return getDefaultProfileImage();
        }
        return Files.readAllBytes(serverFile.toPath());
    }

    private byte[] getDefaultProfileImage () throws IOException {
        System.out.println("getDefaultProfileImage");
        final File serverFile = new File(ROOT_DIR + PROFILE_IMAGES_FOLDER +"/default.jpg");
        System.out.println(serverFile.getAbsolutePath());
        return Files.readAllBytes(serverFile.toPath());
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
    public void getPaper(int id, String token, HttpServletResponse response) throws NoSuchEntityException, ForbiddenException, ServiceErrorException {
        SecureToken secureToken = null;
        if (token != null) {
            secureToken = tokenUtil.parseSecure(token);
        }

        PublicationEntity publication = publicationService.getPublicationById(id);
        if (!publicationValidateService.isPublicationAvailable(publication, secureToken))
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
    private final String PROFILE_IMAGES_FOLDER = "/profiles";
    private final String CONTACT_REQUESTS_ATTACHMENTS_FOLDER = "/contact_requests";
    private final String MESSAGES_ATTACHMENTS_FOLDER = "/messages";

    @Value("${remote_storage.use}")
    private boolean useRemote;

    private static String fullPath(String name, String folder) {
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

}
