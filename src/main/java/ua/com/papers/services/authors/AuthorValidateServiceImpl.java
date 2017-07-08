package ua.com.papers.services.authors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.AuthorCriteria;
import ua.com.papers.criteria.impl.AuthorMasterCriteria;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.utils.SessionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * Created by Andrii on 28.09.2016.
 */
@Component
public class AuthorValidateServiceImpl implements IAuthorValidateService {

    @Autowired
    private Validator validator;

    @Autowired
    private ICriteriaRepository criteriaRepository;

    @Override
    public void authorValidForCreate(AuthorEntity author) throws ServiceErrorException, ValidationException {
        Set<ConstraintViolation<AuthorEntity>> violations = validator.validate(author);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(AuthorEntity.class.getName(), violations);
        }
    }

    @Override
    public void authorMasterValidForCreate(AuthorMasterEntity authorMaster) throws ValidationException, ServiceErrorException {
        Set<ConstraintViolation<AuthorMasterEntity>> violations = validator.validate(authorMaster);
        if(violations != null && !violations.isEmpty())
            throw new ValidationException(AuthorMasterEntity.class.getName(), violations);
        AuthorMasterCriteria cr = new AuthorMasterCriteria(0, 2);
        cr.setLastName(authorMaster.getLastName());
        cr.setInitials(authorMaster.getInitials());
        Criteria<AuthorMasterEntity> criteria = cr;
        List<AuthorMasterEntity> list = criteriaRepository.find(criteria);
        if (list.size()>0)
            throw new ValidationException(AuthorMasterEntity.class.getName(), "AuthorMasterEntity already exists");
    }

    @Override
    public void authorValidForUpdate(AuthorEntity author) throws ValidationException {
        Set<ConstraintViolation<AuthorEntity>> violations = validator.validate(author);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(AuthorEntity.class.getName(), violations);
        }
    }

    @Override
    public void authorMasterValidForUpdate(AuthorMasterEntity entity) throws ValidationException {
        Set<ConstraintViolation<AuthorMasterEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(AuthorMasterEntity.class.getName(), violations);
        }
    }
}
