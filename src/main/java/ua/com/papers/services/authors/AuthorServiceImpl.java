package ua.com.papers.services.authors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.AuthorCriteria;
import ua.com.papers.criteria.impl.AuthorMasterCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.persistence.dao.repositories.AuthorMastersRepository;
import ua.com.papers.persistence.dao.repositories.AuthorsRepository;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 28.09.2016.
 */
@Component
public class AuthorServiceImpl implements IAuthorService {

    @Autowired
    private AuthorMastersRepository mastersRepository;
    @Autowired
    private AuthorsRepository authorsRepository;
    @Autowired
    private Converter<AuthorEntity> authorEntityConverter;
    @Autowired
    private Converter<AuthorMasterEntity> authorMasterEntityConverter;
    @Autowired
    private IAuthorValidateService authorValidateService;
    @Autowired
    private ICriteriaRepository criteriaRepository;

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    public AuthorEntity getAuthorById(int id) throws NoSuchEntityException {
        AuthorEntity author = authorsRepository.findOne(id);
        if (author == null)
            throw new NoSuchEntityException("author","id:"+id);
        return author;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<AuthorEntity> getAuthors(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        Criteria<AuthorEntity> criteria = new AuthorCriteria(offset, limit, restrict);

        List<AuthorEntity> list = criteriaRepository.find(criteria);
        if(list == null || list.isEmpty())
            throw new NoSuchEntityException("authors", String.format("[offset: %d, limit: %d, restriction: %s]", offset, limit, restrict));

        return list;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public AuthorMasterEntity getAuthorMasterById(int id) throws NoSuchEntityException {
        AuthorMasterEntity author = mastersRepository.findOne(id);
        if (author == null)
            throw new NoSuchEntityException("authorMaster","id:"+id);
        return author;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<AuthorMasterEntity> getAuthorMasters(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        Criteria<AuthorMasterEntity> criteria = new AuthorMasterCriteria(offset, limit, restrict);

        List<AuthorMasterEntity> list = criteriaRepository.find(criteria);
        if(list == null || list.isEmpty())
            throw new NoSuchEntityException("authorMasters", String.format("[offset: %d, limit: %d, restriction: %s]", offset, limit, restrict));

        return list;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public Map<String, Object> getAuthorMapById(int id, Set<String> fields) throws NoSuchEntityException {
        return authorEntityConverter.convert(getAuthorById(id),fields);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public Map<String, Object> getAuthorMasterMapId(int id, Set<String> fields) throws NoSuchEntityException {
        return authorMasterEntityConverter.convert(getAuthorMasterById(id),fields);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<Map<String, Object>> getAuthorsMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        return authorEntityConverter.convert(getAuthors(offset, limit, restrict), fields);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<Map<String, Object>> getAuthorsMastersMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        return authorMasterEntityConverter.convert(getAuthorMasters(offset, limit, restrict), fields);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public int createAuthor(AuthorView view) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        AuthorEntity entity = new AuthorEntity();
        merge(entity,view);
        authorValidateService.authorValidForCreate(entity);
        entity= authorsRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    private void merge(AuthorEntity entity, AuthorView view) throws NoSuchEntityException {
        if (view.getId()!=null) entity.setId(view.getId());
        else view.setId(entity.getId());
        if (view.getInitials()!=null) entity.setInitials(view.getInitials());
        else view.setInitials(entity.getInitials());
        if (view.getLast_name()!=null) entity.setLastName(view.getLast_name());
        else view.setLast_name(entity.getLastName());
        if (view.getOriginal()!=null) entity.setOriginal(view.getOriginal());
        else view.setOriginal(entity.getOriginal());
        if (view.getMaster_id()!=null&&view.getMaster_id()>0)
            entity.setMaster(getAuthorMasterById(view.getMaster_id()));
        else if (entity.getMaster()!=null)
            view.setMaster_id(entity.getMaster().getId());
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public int createAuthorMaster(AuthorMasterView view) throws ValidationException, ServiceErrorException, NoSuchEntityException {
        AuthorMasterEntity entity = new AuthorMasterEntity();
        merge(entity,view);
        authorValidateService.authorMasterValidForCreate(entity);
        entity= mastersRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public int updateAuthor(AuthorView authorView) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        if (authorView.getId()==null||authorView.getId()==0)
            throw new ServiceErrorException();
        AuthorEntity authorEntity = getAuthorById(authorView.getId());
        merge(authorEntity,authorView);
        authorValidateService.authorValidForUpdate(authorEntity);
        authorEntity = authorsRepository.saveAndFlush(authorEntity);
        if(authorEntity == null){
            throw new ServiceErrorException();
        }
        return authorEntity.getId();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public int updateAuthorMaster(AuthorMasterView view) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        if (view.getId()==null||view.getId()==0)
            throw new ServiceErrorException();
        AuthorMasterEntity entity = getAuthorMasterById(view.getId());
        merge(entity,view);
        authorValidateService.authorMasterValidForUpdate(entity);
        entity = mastersRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public int countAuthors(String restrict) throws WrongRestrictionException {
        Criteria<AuthorEntity> criteria = new AuthorCriteria(restrict);
        return criteriaRepository.count(criteria);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public int countAuthorsMaster(String restrict) throws WrongRestrictionException {
        Criteria<AuthorMasterEntity> criteria = new AuthorMasterCriteria(restrict);
        return criteriaRepository.count(criteria);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteAuthor(int id) throws NoSuchEntityException {
        AuthorEntity entity = getAuthorById(id);
        authorsRepository.delete(entity);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteMasterAuthor(int id) throws NoSuchEntityException {
        AuthorMasterEntity entity = getAuthorMasterById(id);
        mastersRepository.delete(entity);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    public AuthorMasterEntity findByNameMaster(String lastName, String initials) {
        AuthorMasterCriteria cr = new AuthorMasterCriteria(0, 2);
        cr.setLastName(lastName);
        cr.setInitials(initials);
        Criteria<AuthorMasterEntity> criteria = cr;
        List<AuthorMasterEntity> list = criteriaRepository.find(criteria);
        if (list.size()>0)
            return list.get(0);
        return null;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    public AuthorEntity findByOriginal(String original) {
        AuthorCriteria cr = new AuthorCriteria();
        cr.setOriginal(original);
        Criteria<AuthorEntity> criteria = cr;
        List<AuthorEntity> list = criteriaRepository.find(criteria);
        if (list.size()>0)
            return list.get(0);
        return null;
    }

    @Transactional(propagation=Propagation.REQUIRED)
    private void merge(AuthorMasterEntity entity, AuthorMasterView view) throws NoSuchEntityException {
        if (view.getId()!=null) entity.setId(view.getId());
        else view.setId(entity.getId());
        if (view.getInitials()!=null) entity.setInitials(view.getInitials());
        else view.setInitials(entity.getInitials());
        if (view.getLast_name()!=null) entity.setLastName(view.getLast_name());
        else view.setLast_name(entity.getLastName());
        if(view.getAuthorsIds()!=null&&!view.getAuthorsIds().isEmpty()){
            for (Integer authorId:view.getAuthorsIds())
                entity.addAuthor(getAuthorById(authorId));
        }else if (entity.getAuthors()!=null&&entity.getAuthors().size()>0){
            List<Integer> ids = new ArrayList<>();
            for (AuthorEntity author:entity.getAuthors())
                ids.add(author.getId());
            view.setAuthorsIds(ids);
        }
    }
}
