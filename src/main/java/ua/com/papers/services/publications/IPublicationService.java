package ua.com.papers.services.publications;

import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.PublicationEntity;

import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 26.09.2016.
 */
public interface IPublicationService {

    PublicationEntity getPublicationById(int id);
    Map<String, Object> getUserByIdMap(int userId, Set<String> fields) throws NoSuchEntityException;
}
