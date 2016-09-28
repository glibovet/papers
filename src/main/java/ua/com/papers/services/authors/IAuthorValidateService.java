package ua.com.papers.services.authors;

import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.view.UserView;

/**
 * Created by Andrii on 28.09.2016.
 */
public interface IAuthorValidateService {

    void authorValidForCreate(AuthorEntity author) throws ServiceErrorException, ValidationException;

    void authorMasterValidForCreate(AuthorMasterEntity authorMaster) throws ValidationException, ServiceErrorException;
}
