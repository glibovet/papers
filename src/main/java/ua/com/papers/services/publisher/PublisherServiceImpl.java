package ua.com.papers.services.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.PublisherCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.persistence.dao.repositories.PublisherRepository;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.address.IAddressService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Component
public class PublisherServiceImpl implements IPublisherService{

    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private Converter<PublisherEntity> publisherConverter;
    @Autowired
    private IPublisherValidateService publisherValidateService;
    @Autowired
    private IAddressService addressService;
    @Autowired
    private ICriteriaRepository criteriaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PublisherEntity getPublisherById(int id) throws NoSuchEntityException {
        PublisherEntity entity = publisherRepository.findOne(id);
        if (entity == null)
            throw new NoSuchEntityException("publisher","id:"+id);
        return entity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<PublisherEntity> getPublishers(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        Criteria<PublisherEntity> criteria = new PublisherCriteria(offset, limit, restrict);

        List<PublisherEntity> list = criteriaRepository.find(criteria);
        if(list == null || list.isEmpty())
            throw new NoSuchEntityException("publishers", String.format("[offset: %d, limit: %d, restrict: %s]", offset, limit, restrict));

        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> getPublisherMapById(int id, Set<String> fields) throws NoSuchEntityException {
        return publisherConverter.convert(getPublisherById(id), fields);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Map<String, Object>> getPublishersMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        return publisherConverter.convert(getPublishers(offset, limit, restrict), fields);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int createPublisher(PublisherView view) throws ValidationException, ServiceErrorException, NoSuchEntityException {
        PublisherEntity entity = new PublisherEntity();
        merge(entity,view);
        publisherValidateService.publisherValidForCreate(entity);
        entity= publisherRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    private void merge(PublisherEntity entity, PublisherView view) throws NoSuchEntityException {
        if (view.getId()!=null) entity.setId(view.getId());
        else view.setId(entity.getId());
        if (view.getTitle()!=null&&!"".equals(view.getTitle())) entity.setTitle(view.getTitle());
        else view.setTitle(entity.getTitle());
        if (view.getDescription()!=null&&!"".equals(view.getDescription())) entity.setDescription(view.getDescription());
        else view.setDescription(entity.getDescription());
        if (view.getUrl()!=null&&!"".equals(view.getUrl())) entity.setUrl(view.getUrl());
        else view.setUrl(entity.getUrl());
        if (view.getContacts()!=null&&!"".equals(view.getContacts())) entity.setContacts(view.getContacts());
        else view.setContacts(entity.getContacts());
        if (view.getAddress()!=null){
            entity.setAddress(addressService.getAddressById(view.getAddress()));
        }else if(entity.getAddress()!=null)
            view.setAddress(entity.getAddress().getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updatePublisher(PublisherView view) throws NoSuchEntityException, ServiceErrorException, ValidationException {
        if (view.getId()==null||view.getId()==0)
            throw new ServiceErrorException();
        PublisherEntity entity = getPublisherById(view.getId());
        merge(entity,view);
        publisherValidateService.publisherValidForUpdate(entity);
        entity = publisherRepository.saveAndFlush(entity);
        if(entity == null)
            throw new ServiceErrorException();
        return entity.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int countPublishers(String restrict) throws WrongRestrictionException {
        Criteria<PublisherEntity> criteria = new PublisherCriteria(restrict);

        return criteriaRepository.count(criteria);
    }
}
