package ua.com.papers.services.publications;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.PublicationCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.*;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.storage.IStorageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    private IElasticSearch elasticSearch;

    @Autowired
    private IAuthorService authorService;

    @Autowired
    private ICriteriaRepository criteriaRepository;

    @Autowired
    private IStorageService storageService;

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
    public List<PublicationEntity> getPublications(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        Criteria<PublicationEntity> criteria = new PublicationCriteria(offset, limit, restrict);
        List<PublicationEntity> list = criteriaRepository.find(criteria);
        if(list == null || list.isEmpty())
            throw new NoSuchEntityException("publication", String.format("[offset: %d, limit: %d]", offset, limit));
        return list;
    }

    @Override
    @Transactional
    public List<PublicationEntity> getPublications(int offset, int limit, PublicationCriteria criteria) throws NoSuchEntityException {
        List<PublicationEntity> list = criteriaRepository.find(criteria);
        if(list == null || list.isEmpty())
            throw new NoSuchEntityException("publication", String.format("[offset: %d, limit: %d]", offset, limit));
        return list;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getPublicationsMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        return publicationConverter.convert(getPublications(offset, limit, restrict), fields);
    }

    @Override
    @Transactional
    public int createPublication(PublicationView view) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        PublicationEntity  entity = new PublicationEntity();
        merge(entity,view);
        addAuthors(entity, view);
        publicationValidateService.publicationValidForCreation(entity);
        entity = publicationRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public int updatePublication(PublicationView view) throws NoSuchEntityException, ServiceErrorException, ValidationException, ForbiddenException, ElasticSearchError {
        if (view.getId()==null||view.getId()==0)
            throw new ServiceErrorException();
        PublicationEntity entity = getPublicationById(view.getId());
        merge(entity,view);
        addAuthors(entity, view);
        int id = updatePublication(entity);
        //if (id!=0&&entity.isInIndex())
        //    elasticSearch.indexPublication(id);
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

    @Override
    @Transactional
    public int countPublications(String restriction) throws WrongRestrictionException {
        Criteria<PublicationEntity> criteria = new PublicationCriteria(restriction);
        return criteriaRepository.count(criteria);
    }

    @Override
    @Transactional
    public void removePublicationsFromIndex() {
        publicationRepository.removePublicationsFromIndex();
    }

    @Override
    @Transactional
    public void savePublicationFromRobot(PublicationView publication) throws ValidationException, ServiceErrorException, ElasticSearchError, ForbiddenException, WrongRestrictionException, NoSuchEntityException {

        PublicationEntity fromDb = null;
        PublicationCriteria criteria = new PublicationCriteria("{}");
        criteria.setLink(publication.getLink());
        criteria.setTitle(publication.getTitle());
        List<PublicationEntity> searchResult = null;
        int id=0;
        try {
            searchResult = getPublications(0, 2, criteria);
        } catch (NoSuchEntityException e) {
            id = createPublication(publication);
        }
        if (searchResult!=null&&searchResult.size() >= 1) {
            fromDb = searchResult.get(0);
            if (fromDb.isInIndex())
                return;
            id = fromDb.getId();
        }
        if (id > 0 && publication.getFile_link() != null) {
            String url = publication.getFile_link();
            storageService.uploadPaper(id, url);
            //elasticSearch.indexPublication(id);
        }
    }

    @Override
    @Transactional
    public List<PublicationEntity> getAllPublications() {
        List<PublicationEntity> list = publicationRepository.findAll();
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
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


        if (view.getFile_link() != null && !view.getFile_link().equals(entity.getFileLink())) {
            // should update original file name
            int slash = view.getFile_link().lastIndexOf('/');
            if (slash > -1) {
                entity.setFileNameOriginal(view.getFile_link().substring(slash+1));
            } else {
                entity.setFileNameOriginal(view.getFile_link());
            }
        }
        if (view.getFile_link()!=null&&!"".equals(view.getFile_link())) entity.setFileLink(view.getFile_link());
        else view.setFile_link(entity.getFileLink());

        if (view.getPublisher_id() != null && view.getPublisher_id()!=0){
            entity.setPublisher(publisherService.getPublisherById(view.getPublisher_id()));
        }else if (entity.getPublisher()!=null)
            view.setPublisher_id(entity.getPublisher().getId());

        if (view.getStatus() != null)entity.setStatus(view.getStatus());
        else view.setStatus(entity.getStatus());

        if(view.getAuthors_id() != null && !view.getAuthors_id().isEmpty()) {
            // FIXME: 2/19/2017 remove workaround
            Set<AuthorMasterEntity> entities;

            try {

                StringBuilder sb = new StringBuilder("{\"ids\":[");

                for(val id : view.getAuthors_id()) {
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

    private void addAuthors(PublicationEntity entity, PublicationView view) {
        if (view.getAuthors_id() != null && !view.getAuthors_id().isEmpty()) {
            List<Integer> newAuthors = view.getAuthors_id();
            if (entity.getAuthors() != null) {
                for (AuthorMasterEntity ame : entity.getAuthors()) {
                    newAuthors.remove(ame.getId());
                }
            }

            for (Integer id : newAuthors) {
                try {
                    entity.addAuthor(authorService.getAuthorMasterById(id));
                } catch (NoSuchEntityException e) { }
            }
        }
    }
}
