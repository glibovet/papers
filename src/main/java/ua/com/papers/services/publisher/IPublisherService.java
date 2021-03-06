package ua.com.papers.services.publisher;

import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.view.PublisherView;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
public interface IPublisherService {

    PublisherEntity getPublisherById(int id) throws NoSuchEntityException;
    List<PublisherEntity> getPublishers(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException;
    Map<String, Object> getPublisherMapById(int id, Set<String> fields) throws NoSuchEntityException;
    List<Map<String,Object>> getPublishersMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException;
    int createPublisher(PublisherView view) throws ValidationException, ServiceErrorException, NoSuchEntityException, WrongRestrictionException;
    int updatePublisher(PublisherView view) throws NoSuchEntityException, ServiceErrorException, ValidationException;

    int countPublishers(String restrict) throws WrongRestrictionException;

    void deletePublisher(int id) throws NoSuchEntityException;

    PublisherEntity findPublisherByTitle(String title) throws WrongRestrictionException;
}
