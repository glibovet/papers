package ua.com.papers.storage;

import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.utils.ResultCallback;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

/**
 * Created by Andrii on 05.10.2016.
 */
public interface IStorageService {
    /**
     * Uploads publication on a storage. Result callback will
     * return either {@linkplain File} which represents file path on the storage,
     * or throw error
     */
    void uploadPaper(@NotNull PublicationEntity publication, @NotNull ResultCallback<File> callback);

    boolean uploadPaper(int id, String url) throws NoSuchEntityException, StorageException;

    boolean uploadPaper(int id, MultipartFile file) throws NoSuchEntityException, ServiceErrorException, IOException, ValidationException;

    boolean uploadProfileImage(UserEntity user, MultipartFile file) throws NoSuchEntityException, ServiceErrorException, IOException, ValidationException;

    byte[] getProfileImage (int userId) throws IOException;

    byte[] getPaperAsByteArray(Integer paperId) throws NoSuchEntityException, ServiceErrorException, ForbiddenException;

    byte[] getPaperAsByteArray(PublicationEntity entity) throws NoSuchEntityException, ServiceErrorException, ForbiddenException;

    void getPaper(int id, String token, HttpServletResponse response) throws NoSuchEntityException, ForbiddenException, ServiceErrorException;

    boolean paperHasFile(int id) throws NoSuchEntityException, ForbiddenException, ServiceErrorException;

    boolean uploadRequestAttachment(ContactEntity contact, MultipartFile file) throws IOException, ServiceErrorException;

    void getContactRequestAttachment (HttpServletResponse response, ContactEntity contact) throws IOException;

    boolean uploadMessageAttachment(MessageEntity message, MultipartFile file) throws IOException, ServiceErrorException;
}
