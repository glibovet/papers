package ua.com.papers.services.authors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.dao.repositories.AuthorMastersRepository;
import ua.com.papers.persistence.dao.repositories.AuthorsRepository;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.services.utils.SessionUtils;

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

    @Override
    @Transactional
    public AuthorEntity getAuthorById(int id) throws NoSuchEntityException {
        AuthorEntity author = authorsRepository.findOne(id);
        if (author == null)
            throw new NoSuchEntityException("author","id:"+id);
        return author;
    }

    @Override
    @Transactional
    public List<AuthorEntity> getAuthors(int offset, int limit) throws NoSuchEntityException {
        if (limit==0)
            limit=20;
        Page<AuthorEntity> list = authorsRepository.findAll(new PageRequest(offset/limit,limit));
        if(list == null || list.getContent().isEmpty())
            throw new NoSuchEntityException("authors", String.format("[offset: %d, limit: %d]", offset, limit));
        return list.getContent();
    }

    @Override
    @Transactional
    public AuthorMasterEntity getAuthorMasterById(int id) throws NoSuchEntityException {
        AuthorMasterEntity author = mastersRepository.findOne(id);
        if (author == null)
            throw new NoSuchEntityException("authorMaster","id:"+id);
        return author;
    }

    @Override
    @Transactional
    public List<AuthorMasterEntity> getAuthorMasters(int offset, int limit) throws NoSuchEntityException {
        Page<AuthorMasterEntity> list = mastersRepository.findAll(new PageRequest(offset/limit,limit));
        if(list == null || list.getContent().isEmpty())
            throw new NoSuchEntityException("authorMasters", String.format("[offset: %d, limit: %d]", offset, limit));
        return list.getContent();
    }

    @Override
    @Transactional
    public Map<String, Object> getAuthorMapById(int id, Set<String> fields) throws NoSuchEntityException {
        return authorEntityConverter.convert(getAuthorById(id),fields);
    }

    @Override
    @Transactional
    public Map<String, Object> getAuthorMasterMapId(int id, Set<String> fields) throws NoSuchEntityException {
        return authorMasterEntityConverter.convert(getAuthorMasterById(id),fields);
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getAuthorsMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException {
        return authorEntityConverter.convert(getAuthors(offset,limit),fields);
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getAuthorsMastersMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException {
        return authorMasterEntityConverter.convert(getAuthorMasters(offset, limit),fields);
    }

    @Override
    @Transactional
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
        if (view.getLastName()!=null) entity.setLastName(view.getLastName());
        else view.setLastName(entity.getLastName());
        if (view.getOriginal()!=null) entity.setOriginal(view.getOriginal());
        else view.setOriginal(entity.getOriginal());
        if (view.getMasterId()!=null&&view.getMasterId()>0)
            entity.setMaster(getAuthorMasterById(view.getMasterId()));
        else if (entity.getMaster()!=null)
            view.setMasterId(entity.getMaster().getId());
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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
