package ua.com.papers.services.authors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.utils.SessionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by Andrii on 28.09.2016.
 */
@Component
public class AuthorValidateServiceImpl implements IAuthorValidateService {

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private Validator validator;

    @Override
    public void authorValidForCreate(AuthorEntity author) throws ServiceErrorException, ValidationException {
        Set<ConstraintViolation<AuthorEntity>> violations = validator.validate(author);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(AuthorEntity.class.getName(), violations);
        }
        if (!sessionUtils.isUserWithRole(RolesEnum.admin)){
            throw new ServiceErrorException();
        }
    }

    @Override
    public void authorMasterValidForCreate(AuthorMasterEntity authorMaster) throws ValidationException, ServiceErrorException {
        Set<ConstraintViolation<AuthorMasterEntity>> violations = validator.validate(authorMaster);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(AuthorMasterEntity.class.getName(), violations);
        }
        if (!sessionUtils.isUserWithRole(RolesEnum.admin)){
            throw new ServiceErrorException();
        }
    }
}
