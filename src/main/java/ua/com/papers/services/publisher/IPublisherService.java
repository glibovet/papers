package ua.com.papers.services.publisher;

import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
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
    List<PublisherEntity> getPublishers(int offset, int limit) throws NoSuchEntityException;
    Map<String, Object> getPublisherMapById(int id, Set<String> fields) throws NoSuchEntityException;
    List<Map<String,Object>> getPublishersMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException;
    int createPublisher(PublisherView view) throws ValidationException, ServiceErrorException, NoSuchEntityException;
    int updatePublisher(PublisherView view) throws NoSuchEntityException, ServiceErrorException, ValidationException;
}
