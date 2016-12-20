package ua.com.papers.storage;

import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Andrii on 05.10.2016.
 */
public interface IStorageService {
    boolean uploadPaper(int id, MultipartFile file) throws NoSuchEntityException, ServiceErrorException, IOException, ValidationException;
    byte[] getPaperAsByteArray(Integer paperId) throws NoSuchEntityException, ServiceErrorException, ForbiddenException;
    byte[] getPaperAsByteArray(PublicationEntity entity) throws NoSuchEntityException, ServiceErrorException, ForbiddenException;

    void getPaper(int id, HttpServletResponse response) throws NoSuchEntityException, ForbiddenException, ServiceErrorException;

    boolean paperHasFile(int id) throws NoSuchEntityException, ForbiddenException, ServiceErrorException;
}
