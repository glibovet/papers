package ua.com.papers.services.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.PublicationCriteria;
import ua.com.papers.criteria.impl.PublicationOrderCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.AuthRequiredException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.persistence.dao.repositories.PublicationOrderRepository;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationOrderEntity;
import ua.com.papers.pojo.enums.EmailTypes;
import ua.com.papers.pojo.enums.PublicationOrderStatusEnum;
import ua.com.papers.pojo.view.PublicationOrderView;
import ua.com.papers.services.mailing.IMailingService;
import ua.com.papers.services.utils.SessionUtils;

import java.util.*;

/**
 * Created by Andrii on 20.05.2017.
 */
@Service
@Transactional(propagation= Propagation.REQUIRED)
public class PublicationOrderServiceImpl implements IPublicationOrderService{

    @Autowired
    private IPublicationOrderService service;
    @Autowired
    private PublicationOrderRepository repository;
    @Autowired
    private Converter<PublicationOrderEntity> converter;
    @Autowired
    private IPublicationOrderValidationService validationService;
    @Autowired
    private ICriteriaRepository criteriaRepository;
    @Autowired
    private IPublicationService publicationService;
    @Autowired
    private IMailingService malingService;

    @Override
    public Map<String, Object> getPublicationOrderMapById(int id, Set<String> fields) throws NoSuchEntityException {
        return converter.convert(getPublicationOrderById(id),fields);
    }

    @Override
    public PublicationOrderEntity getPublicationOrderById(int id) throws NoSuchEntityException {
        PublicationOrderEntity publicationOrder = repository.findOne(id);
        if (publicationOrder == null)
            throw new NoSuchEntityException("publicationOrder", "id: " + id);
        return publicationOrder;
    }

    @Override
    public List<PublicationOrderEntity> getPublicationOrders(int offset, int limit, String restrict) throws WrongRestrictionException, NoSuchEntityException {
        Criteria<PublicationOrderEntity> criteria = new PublicationOrderCriteria(offset, limit, restrict);
        List<PublicationOrderEntity> list = criteriaRepository.find(criteria);
        if(list == null || list.isEmpty())
            throw new NoSuchEntityException("publicationOrder", String.format("[offset: %d, limit: %d]", offset, limit));
        return list;
    }

    @Override
    public List<Map<String, Object>> getPublicationOrdersMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        return converter.convert(getPublicationOrders(offset, limit, restrict), fields);
    }

    @Override
    public int create(PublicationOrderView view) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        PublicationOrderEntity  entity = new PublicationOrderEntity();
        merge(entity,view);
        validationService.validForCreation(entity);
        entity = repository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    private void merge(PublicationOrderEntity entity, PublicationOrderView view) throws NoSuchEntityException {
        if (view.getId()!=null) entity.setId(view.getId());
        else view.setId(entity.getId());

        if (view.getEmail()!=null&&!"".equals(view.getEmail())) entity.setEmail(view.getEmail());
        else view.setEmail(entity.getEmail());

        if (view.getReason()!=null&&!"".equals(view.getReason())) entity.setReason(view.getReason());
        else view.setReason(entity.getReason());

        if (view.getAnswer()!=null&&!"".equals(view.getAnswer())) entity.setAnswer(view.getAnswer());
        else view.setAnswer(entity.getAnswer());

        if (view.getStatus()!=null) entity.setStatus(view.getStatus());
        else view.setStatus(entity.getStatus());

        if (view.getPublicationId()!=null)
            entity.setPublication(publicationService.getPublicationById(view.getPublicationId()));
        else if (entity.getPublication()!=null)
            view.setPublicationId(entity.getPublication().getId());
    }

    @Override
    public int answer(PublicationOrderView view) throws ServiceErrorException, NoSuchEntityException, ValidationException, AuthRequiredException {
        if (view.getId()==null||view.getId()==0)
            throw new ServiceErrorException();
        PublicationOrderEntity entity = getPublicationOrderById(view.getId());
        merge(entity,view);
        validationService.validForUpdate(entity);
        entity = repository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        Map<String,String> data = new HashMap<>();
        if (entity.getStatus()== PublicationOrderStatusEnum.APPLIED){
            data.put("PUBLICATION_ID",entity.getPublication().getId().toString());
            malingService.sendEmailToUser(EmailTypes.approve_publication_order,entity.getEmail(), data, Locale.ENGLISH);
        }else if (entity.getStatus()== PublicationOrderStatusEnum.REJECTED){

            data.put("REJECT_REASON",entity.getReason());
            malingService.sendEmailToUser(EmailTypes.reject_publication_order,entity.getEmail(), data, Locale.ENGLISH);
        }
        return entity.getId();
    }

    @Override
    public Integer count(String restrict) throws WrongRestrictionException {
        Criteria<PublicationOrderEntity> criteria = new PublicationOrderCriteria(restrict);
        return criteriaRepository.count(criteria);
    }
}
