package ua.com.papers.services.publications;

import ua.com.papers.criteria.impl.PublicationCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ElasticSearchException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.utils.ResultCallback;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 26.09.2016.
 */
public interface IPublicationService {

    PublicationEntity getPublicationById(int id) throws NoSuchEntityException;
    Map<String, Object> getPublicationByIdMap(int id, Set<String> fields) throws NoSuchEntityException;

    List<PublicationEntity> getPublications(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException;
    List<PublicationEntity> getPublications(PublicationCriteria criteria) throws NoSuchEntityException;

    List<Map<String, Object>> getPublicationsMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException;
    List<Map<String, Object>> getPublicationsMap(Set<String> fields, PublicationCriteria criteria) throws NoSuchEntityException;

    int createPublication(PublicationView view) throws ServiceErrorException, NoSuchEntityException, ValidationException;

    int updatePublication(PublicationView view) throws NoSuchEntityException, ServiceErrorException, ValidationException, ForbiddenException, ElasticSearchException;
    int updatePublication(PublicationEntity view) throws ServiceErrorException, ValidationException;

    int countPublications(String restriction) throws WrongRestrictionException;
    int countPublications(PublicationCriteria criteria);

    void removePublicationsFromIndex();

    /**
     * Saves publication in async manner. In a contrary to {@link #savePublicationFromRobot(PublicationView)}
     * upload error to the storage won't be swallowed
     * @param publication
     * @param callback
     */
    void savePublicationFromRobot(@NotNull PublicationView publication, @Nullable ResultCallback<PublicationEntity> callback);

    void savePublicationFromRobot(PublicationView publication) throws NoSuchEntityException, ValidationException, ServiceErrorException, ElasticSearchException, ForbiddenException, WrongRestrictionException;

    List<PublicationEntity> getAllPublications();
}
