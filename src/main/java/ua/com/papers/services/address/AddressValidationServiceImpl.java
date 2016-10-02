package ua.com.papers.services.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AddressEntity;
import ua.com.papers.pojo.entities.PublisherEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Service
public class AddressValidationServiceImpl implements IAddressValidationService{

    @Autowired
    private Validator validator;

    @Override
    public void addressValidForCreation(AddressEntity entity) throws ValidationException {
        Set<ConstraintViolation<AddressEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(AddressEntity.class.getName(), violations);
        }
    }

    @Override
    public void addressValidForUpdate(AddressEntity entity) throws ValidationException {
        Set<ConstraintViolation<AddressEntity>> violations = validator.validate(entity);
        if(violations != null && !violations.isEmpty()) {
            throw new ValidationException(AddressEntity.class.getName(), violations);
        }
    }
}
