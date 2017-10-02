package ua.com.papers.services.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.PublisherCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.PublisherEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Service
public class PublisherValidateServiceImpl implements IPublisherValidateService{
    @Autowired
    private Validator validator;
    @Autowired
    private ICriteriaRepository criteriaRepository;

    @Override
    @Transactional
    public void publisherValidForCreate(PublisherEntity entity) throws ValidationException, WrongRestrictionException {
        Set<ConstraintViolation<PublisherEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublisherEntity.class.getName(), violations);
        }
        PublisherCriteria pc = new PublisherCriteria("{}");
        pc.setTitle(entity.getTitle());
        Criteria<PublisherEntity> criteria = pc;
        List<PublisherEntity> list = criteriaRepository.find(criteria);
        if(list != null && !list.isEmpty())
            throw new ValidationException(PublisherEntity.class.getName(), "Such entity exist");
    }

    @Override
    public void publisherValidForUpdate(PublisherEntity entity) throws ValidationException {
        Set<ConstraintViolation<PublisherEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(PublisherEntity.class.getName(), violations);
        }
    }
}
