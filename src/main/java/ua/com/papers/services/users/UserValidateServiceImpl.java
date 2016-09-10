package ua.com.papers.services.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.utils.SessionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by Andrii on 10.09.2016.
 */
@Service
public class UserValidateServiceImpl implements IUserValidateService {

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private Validator validator;

    @Override
    public void validForCreate(UserView user) throws ServiceErrorException, ValidationException {
        Set<ConstraintViolation<UserView>> violations = validator.validate(user);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(UserEntity.class.getName(), violations);
        }
        if (!sessionUtils.isAuthorized()){
            if (user.getRole()==null||!user.getRole().equals(RolesEnum.user)){
                throw new ServiceErrorException();
            }
        }else if (!sessionUtils.isUserWithRole(RolesEnum.admin)){
            throw new ServiceErrorException();
        }
    }
}
