package ua.com.papers.services.publications;

import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.AuthRequiredException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationOrderEntity;
import ua.com.papers.pojo.view.PublicationOrderView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 20.05.2017.
 */
public interface IPublicationOrderService {
    Map<String,Object> getPublicationOrderMapById(int id, Set<String> fields) throws NoSuchEntityException;
    PublicationOrderEntity getPublicationOrderById(int id) throws NoSuchEntityException;

    List<PublicationOrderEntity> getPublicationOrders(int offset, int limit, String restrict) throws WrongRestrictionException, NoSuchEntityException;
    List<Map<String,Object>> getPublicationOrdersMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException;

    int create(PublicationOrderView view) throws ServiceErrorException, NoSuchEntityException, ValidationException;

    int answer(PublicationOrderView view) throws ServiceErrorException, NoSuchEntityException, ValidationException, AuthRequiredException;
    int update(PublicationOrderView view) throws ServiceErrorException, NoSuchEntityException, ValidationException, AuthRequiredException;

    Integer count(String restrict) throws WrongRestrictionException;
}
