package ua.com.papers.services.publisher;

import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublisherEntity;

/**
 * Created by Andrii on 02.10.2016.
 */
public interface IPublisherValidateService {
    void publisherValidForCreate(PublisherEntity entity) throws ValidationException, WrongRestrictionException;
    void publisherValidForUpdate(PublisherEntity entity) throws ValidationException;
}
