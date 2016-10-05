package ua.com.papers.storage;

import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;

import java.io.IOException;

/**
 * Created by Andrii on 05.10.2016.
 */
public interface IStorageService {
    boolean uploadPaper(int id, MultipartFile file) throws NoSuchEntityException, ServiceErrorException, IOException, ValidationException;
}
