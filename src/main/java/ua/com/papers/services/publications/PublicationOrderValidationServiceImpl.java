package ua.com.papers.services.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.AuthRequiredException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationOrderEntity;
import ua.com.papers.pojo.enums.PublicationOrderStatusEnum;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.utils.SessionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by Andrii on 20.05.2017.
 */
@Service
public class PublicationOrderValidationServiceImpl implements IPublicationOrderValidationService {

    @Autowired
    private Validator validator;
    @Autowired
    private SessionUtils sessionUtils;

    @Override
    public void validForCreation(PublicationOrderEntity entity) throws ValidationException, ServiceErrorException {
        Set<ConstraintViolation<PublicationOrderEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublicationOrderEntity.class.getName(), violations);
        }
        if (entity.getPublication()==null)
            throw new ServiceErrorException();
    }

    @Override
    public void validForUpdate(PublicationOrderEntity entity) throws ValidationException, AuthRequiredException, ServiceErrorException {
        sessionUtils.authorized();
        sessionUtils.isUserWithRole(RolesEnum.admin,RolesEnum.moderator);
        Set<ConstraintViolation<PublicationOrderEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublicationOrderEntity.class.getName(), violations);
        }
        if ((entity.getStatus() != PublicationOrderStatusEnum.APPLIED && entity.getStatus() != PublicationOrderStatusEnum.REJECTED) || entity.getPublication()==null)
            throw new ServiceErrorException();
    }
}
