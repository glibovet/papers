package ua.com.papers.services.users;

import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.UserView;

/**
 * Created by Andrii on 10.09.2016.
 */
public interface IUserValidateService {

    void validForCreate(UserView user) throws ServiceErrorException, ValidationException;
}
