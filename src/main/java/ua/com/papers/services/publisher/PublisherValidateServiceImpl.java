package ua.com.papers.services.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.PublisherEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Service
public class PublisherValidateServiceImpl implements IPublisherValidateService{
    @Autowired
    private Validator validator;

    @Override
    public void publisherValidForCreate(PublisherEntity entity) throws ValidationException {
        Set<ConstraintViolation<PublisherEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublisherEntity.class.getName(), violations);
        }
    }

    @Override
    public void publisherValidForUpdate(PublisherEntity entity) throws ValidationException {
        Set<ConstraintViolation<PublisherEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublisherEntity.class.getName(), violations);
        }
    }
}
