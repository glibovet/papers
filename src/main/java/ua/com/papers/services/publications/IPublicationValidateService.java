package ua.com.papers.services.publications;

import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.utils.SecureToken;

/**
 * Created by Andrii on 02.10.2016.
 */
public interface IPublicationValidateService {
    void publicationValidForCreation(PublicationEntity entity) throws ValidationException;
    void publicationValidForUpdate(PublicationEntity entity) throws ValidationException;
    boolean isPublicationAvailableForSearch(PublicationEntity entity);
    boolean isPublicationAvailable(PublicationEntity entity);
    boolean isPublicationAvailable(PublicationEntity entity, SecureToken token);
}
