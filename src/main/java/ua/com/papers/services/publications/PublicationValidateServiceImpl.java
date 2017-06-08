package ua.com.papers.services.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.utils.SessionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Service
public class PublicationValidateServiceImpl implements IPublicationValidateService{
    @Autowired
    private Validator validator;
    @Autowired
    private SessionUtils sessionUtils;

    @Override
    public void publicationValidForCreation(PublicationEntity entity) throws ValidationException {
        Set<ConstraintViolation<PublicationEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublicationEntity.class.getName(), violations);
        }
    }

    @Override
    public void publicationValidForUpdate(PublicationEntity entity) throws ValidationException {
        Set<ConstraintViolation<PublicationEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublicationEntity.class.getName(), violations);
        }
    }

    @Override
    public boolean isPublicationAvailableForSearch(PublicationEntity entity) {
        if (entity == null)
            return false;
        if (entity.getStatus().equals(PublicationStatusEnum.ACTIVE)&&entity.isInIndex())
            return true;
        return false;

    }

    @Override
    public boolean isPublicationAvailable(PublicationEntity entity) {
        if (entity == null)
            return false;
        if (entity.getStatus().equals(PublicationStatusEnum.ACTIVE))
            return true;
        if(!sessionUtils.isUserWithRole(RolesEnum.moderator, RolesEnum.admin)
                && (PublicationStatusEnum.IN_PROCESS.equals(entity.getStatus())
                || PublicationStatusEnum.DELETED.equals(entity.getStatus()))){
            return false;
        }
        return true;
    }

}
