package ua.com.papers.services.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.utils.SecureToken;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Date;
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

    @Override
    public boolean isPublicationAvailable(PublicationEntity entity, SecureToken token) {
        if (token == null) {
            return isPublicationAvailable(entity);
        }

        if (entity.getStatus() != PublicationStatusEnum.ACTIVE) {
            return false;
        }

        Object timeObject = token.getData().get("DATE");
        if (timeObject != null) {
            long timeCreated = (Long)timeObject;

            if (new Date().getTime() - timeCreated > MAX_TILE_TOKEN_LIVE) {
                return false;
            }
        } else {
            // some how token does not have date key
            return false;
        }

        return true;
    }

    // 7 days
    private static final long MAX_TILE_TOKEN_LIVE = 1000 * 60 * 60 * 24 * 7;
}
