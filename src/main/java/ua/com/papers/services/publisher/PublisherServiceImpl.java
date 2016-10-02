package ua.com.papers.services.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ua.com.papers.convertors.Converter;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
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

    @Override
    public PublisherEntity getPublisherById(int id) throws NoSuchEntityException {
        PublisherEntity entity = publisherRepository.findOne(id);
        if (entity == null)
            throw new NoSuchEntityException("publisher","id:"+id);
        return entity;
    }

    @Override
    public List<PublisherEntity> getPublishers(int offset, int limit) throws NoSuchEntityException {
        if (limit==0)
            limit=20;
        Page<PublisherEntity> list = publisherRepository.findAll(new PageRequest(offset/limit,limit));
        if(list == null || list.getContent().isEmpty())
            throw new NoSuchEntityException("publishers", String.format("[offset: %d, limit: %d]", offset, limit));
        return list.getContent();
    }

    @Override
    public Map<String, Object> getPublisherMapById(int id, Set<String> fields) throws NoSuchEntityException {
        return publisherConverter.convert(getPublisherById(id),fields);
    }

    @Override
    public List<Map<String, Object>> getPublishersMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException {
        return publisherConverter.convert(getPublishers(offset,limit),fields);
    }

    @Override
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
}
