package ua.com.papers.services.publications;

import ua.com.papers.exceptions.service_error.AuthRequiredException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationOrderEntity;

/**
 * Created by Andrii on 20.05.2017.
 */
public interface IPublicationOrderValidationService {
    void validForCreation(PublicationOrderEntity entity) throws ValidationException, ServiceErrorException;

    void validForUpdate(PublicationOrderEntity entity) throws ValidationException, AuthRequiredException, ServiceErrorException;
}
