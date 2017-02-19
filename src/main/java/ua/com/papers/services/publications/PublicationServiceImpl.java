package ua.com.papers.services.publications;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publisher.IPublisherService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 26.09.2016.
 */
@Service
public class PublicationServiceImpl implements IPublicationService{

    @Autowired
    private Converter<PublicationEntity> publicationConverter;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private IPublisherService publisherService;

    @Autowired
    private IPublicationValidateService publicationValidateService;

    @Autowired
    private IAuthorService authorService;

    @Override
    @Transactional
    public PublicationEntity getPublicationById(int id) throws NoSuchEntityException {
        PublicationEntity publication = publicationRepository.findOne(id);
        if (publication == null)
            throw new NoSuchEntityException("publication", "id: " + id);
        return publication;
    }

    @Override
    @Transactional
    public Map<String, Object> getPublicationByIdMap(int id, Set<String> fields) throws NoSuchEntityException {
        return publicationConverter.convert(getPublicationById(id),fields);
    }

    @Override
    @Transactional
    public List<PublicationEntity> getPublications(int offset, int limit) throws NoSuchEntityException {
        Page<PublicationEntity> list = publicationRepository.findAll(new PageRequest(offset/limit,limit));
        if(list == null || list.getContent().isEmpty())
            throw new NoSuchEntityException("publication", String.format("[offset: %d, limit: %d]", offset, limit));
        return list.getContent();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getPublicationsMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException {
        return publicationConverter.convert(getPublications(offset, limit), fields);
    }

    @Override
    @Transactional
    public int createPublication(PublicationView view) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        PublicationEntity  entity = new PublicationEntity();
        merge(entity,view);
        publicationValidateService.publicationValidForCreation(entity);
        entity = publicationRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public int updatePublication(PublicationView view) throws NoSuchEntityException, ServiceErrorException, ValidationException {
        if (view.getId()==null||view.getId()==0)
            throw new ServiceErrorException();
        PublicationEntity entity = getPublicationById(view.getId());
        merge(entity,view);
        return updatePublication(entity);
    }

    @Override
    @Transactional
    public int updatePublication(PublicationEntity entity) throws ServiceErrorException, ValidationException {
        if (entity==null||entity.getId()==0)
            throw new ServiceErrorException();
        publicationValidateService.publicationValidForUpdate(entity);
        entity = publicationRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    private void merge(PublicationEntity entity, PublicationView view) throws NoSuchEntityException {
        if (view.getId()!=null) entity.setId(view.getId());
        else view.setId(entity.getId());
        if (view.getTitle()!=null&&!"".equals(view.getTitle())) entity.setTitle(view.getTitle());
        else view.setTitle(entity.getTitle());
        if (view.getAnnotation()!=null&&!"".equals(view.getAnnotation())) entity.setAnnotation(view.getAnnotation());
        else view.setAnnotation(entity.getAnnotation());
        if (view.getType()!=null) entity.setType(view.getType());
        else view.setType(entity.getType());
        if (view.getLink()!=null&&!"".equals(view.getLink())) entity.setLink(view.getLink());
        else view.setLink(entity.getLink());
        if (view.getPublisherId()!=0){
            entity.setPublisher(publisherService.getPublisherById(view.getPublisherId()));
        }else if (entity.getPublisher()!=null)
            view.setPublisherId(entity.getPublisher().getId());
        if(view.getAuthorsId() != null && !view.getAuthorsId().isEmpty()) {
            // FIXME: 2/19/2017 remove workaround
            Set<AuthorMasterEntity> entities;

            try {

                StringBuilder sb = new StringBuilder("{\"ids\":[");

                for(val id : view.getAuthorsId()) {
                    sb.append("\"").append(id.intValue()).append("\",");
                }

                sb.setLength(sb.length() - 1);
                sb.append("]}");

                entities = new HashSet<>(authorService.getAuthorMasters(0, -1, sb.toString()));
            } catch (WrongRestrictionException e) {
                throw new RuntimeException("failed to get authors", e);
            }
            entity.setAuthors(entities);
        }
    }

}
