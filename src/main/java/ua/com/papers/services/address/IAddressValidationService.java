package ua.com.papers.services.address;

import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AddressEntity;

/**
 * Created by Andrii on 02.10.2016.
 */
public interface IAddressValidationService {

    void addressValidForCreation(AddressEntity entity) throws ValidationException;
    void addressValidForUpdate(AddressEntity entity) throws ValidationException;
}
