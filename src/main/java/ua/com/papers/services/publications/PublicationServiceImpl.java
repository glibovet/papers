package ua.com.papers.services.publications;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.crawler.core.main.util.UrlUtils;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.PublicationCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ElasticSearchException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.enums.UploadStatus;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.storage.IStorageService;
import ua.com.papers.utils.ResultCallback;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Created by Andrii on 26.09.2016.
 */
@Service
@Log
public class PublicationServiceImpl implements IPublicationService {

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

    private final ExecutorService executorService;

    @Autowired
    public PublicationServiceImpl(Handler handler) {
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            val th = new Thread(r, "Publication service upload thread");
            th.setUncaughtExceptionHandler((t, e) -> log.log(Level.WARNING, "uncaught exception", e));
            return th;
        });

        log.addHandler(handler);
    }

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
        return publicationConverter.convert(getPublicationById(id), fields);
    }

    @Override
    @Transactional
    public List<PublicationEntity> getPublications(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        Criteria<PublicationEntity> criteria = new PublicationCriteria(offset, limit, restrict);
        List<PublicationEntity> list = criteriaRepository.find(criteria);
        if (list == null || list.isEmpty())
            throw new NoSuchEntityException("publication", String.format("[offset: %d, limit: %d]", offset, limit));
        return list;
    }

    @Override
    @Transactional
    public List<PublicationEntity> getPublications(PublicationCriteria criteria) throws NoSuchEntityException {
        List<PublicationEntity> list = criteriaRepository.find(criteria);
        if (list == null || list.isEmpty())
            throw new NoSuchEntityException("publication", String.format("[offset: %d, limit: %d]", criteria.getOffset(), criteria.getLimit()));
        return list;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getPublicationsMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException {
        return publicationConverter.convert(getPublications(offset, limit, restrict), fields);
    }

    @Override
    public List<Map<String, Object>> getPublicationsMap(Set<String> fields, PublicationCriteria criteria) throws NoSuchEntityException {
        return publicationConverter.convert(getPublications(criteria), fields);
    }

    @Override
    @Transactional
    public int createPublication(PublicationView view) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        return doCreatePublication(view).getId();
    }

    @Override
    @Transactional
    public int updatePublication(PublicationView view) throws NoSuchEntityException, ServiceErrorException, ValidationException, ForbiddenException, ElasticSearchException {
        if (view.getId() == null || view.getId() == 0)
            throw new ServiceErrorException();
        PublicationEntity entity = getPublicationById(view.getId());
        merge(entity, view);
        addAuthors(entity, view);
        int id = updatePublication(entity);
        //if (id!=0&&entity.isInIndex())
        //    elasticSearch.indexPublication(id);
        return updatePublication(entity);
    }

    @Override
    @Transactional
    public int updatePublication(PublicationEntity entity) throws ServiceErrorException, ValidationException {
        if (entity == null || entity.getId() == 0)
            throw new ServiceErrorException();
        publicationValidateService.publicationValidForUpdate(entity);
        entity = publicationRepository.saveAndFlush(entity);
        if (entity == null) {
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
    public int countPublications(PublicationCriteria criteria){
        return criteriaRepository.count(criteria);
    }

    @Override
    @Transactional
    public void removePublicationsFromIndex() {
        publicationRepository.removePublicationsFromIndex();
    }

    @Override
    public void savePublicationFromRobot(@NotNull PublicationView publication, @Nullable ResultCallback<PublicationEntity> callback) {
        /*executorService.submit(() -> {
            try {*/
                doSavePublicationFromRobot(publication, callback);
            /*} catch (Throwable t) {
                t.printStackTrace();
            }

        });*/
    }

    @Override
    @Transactional
    public void savePublicationFromRobot(PublicationView publication) throws ValidationException, ServiceErrorException, ElasticSearchException, ForbiddenException, WrongRestrictionException, NoSuchEntityException {

        PublicationEntity fromDb = null;
        PublicationCriteria criteria = new PublicationCriteria("{}");
        criteria.setLink(publication.getLink());
        criteria.setTitle(publication.getTitle());
        criteria.setOffset(0);
        criteria.setLimit(2);
        List<PublicationEntity> searchResult = null;
        int id = 0;
        try {
            searchResult = getPublications(criteria);
        } catch (NoSuchEntityException e) {
            id = createPublication(publication);
        }
        if (searchResult != null && searchResult.size() >= 1) {
            fromDb = searchResult.get(0);
            if (fromDb.isInIndex())
                return;
            id = fromDb.getId();
        }
        if (id > 0 && publication.getFile_link() != null) {
            String url = publication.getFile_link();
            storageService.uploadPaper(id, url);
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
        if (view.getId() != null) entity.setId(view.getId());
        else view.setId(entity.getId());

        if (view.getTitle() != null && !"".equals(view.getTitle())) entity.setTitle(view.getTitle());
        else view.setTitle(entity.getTitle());

        if (view.getAnnotation() != null && !"".equals(view.getAnnotation()))
            entity.setAnnotation(view.getAnnotation());
        else view.setAnnotation(entity.getAnnotation());

        if (view.getType() != null) entity.setType(view.getType());
        else view.setType(entity.getType());

        if (view.getLink() != null && !"".equals(view.getLink())) entity.setLink(view.getLink());
        else view.setLink(entity.getLink());


        if (view.getFile_link() != null && !view.getFile_link().equals(entity.getFileLink())) {
            // should update original file name
            int slash = view.getFile_link().lastIndexOf('/');
            if (slash > -1) {
                entity.setFileNameOriginal(view.getFile_link().substring(slash + 1));
            } else {
                entity.setFileNameOriginal(view.getFile_link());
            }
        }
        if (view.getFile_link() != null && !"".equals(view.getFile_link())) entity.setFileLink(view.getFile_link());
        else view.setFile_link(entity.getFileLink());

        if (view.getPublisher_id() != null && view.getPublisher_id() != 0) {
            entity.setPublisher(publisherService.getPublisherById(view.getPublisher_id()));
        } else if (entity.getPublisher() != null)
            view.setPublisher_id(entity.getPublisher().getId());

        if (view.getStatus() != null) entity.setStatus(view.getStatus());
        else view.setStatus(entity.getStatus());

        if (view.getAuthors_id() != null && !view.getAuthors_id().isEmpty()) {
            // FIXME: 2/19/2017 remove workaround
            Set<AuthorMasterEntity> entities;

            try {

                StringBuilder sb = new StringBuilder("{\"ids\":[");

                for (val id : view.getAuthors_id()) {
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
                } catch (NoSuchEntityException e) {
                }
            }
        }
    }

    @SneakyThrows(MalformedURLException.class)
    private void doSavePublicationFromRobot(PublicationView publication, ResultCallback<PublicationEntity> callback) {
        Preconditions.checkNotNull(publication);
        Preconditions.checkArgument(!TextUtils.isEmpty(publication.getFile_link()), "Invalid publication view %s", publication);
        Optional<PublicationEntity> entity = Optional.empty();
        Optional<Exception> exception = Optional.empty();

        try {
            val searchResult = getPublications(PublicationServiceImpl.newCriteriaForPublication(publication));

            if (searchResult.isEmpty()) {
                // not found, let's create it
                entity = Optional.of(doCreatePublication(publication));
            } else {
                entity = Optional.of(searchResult.get(0));
            }
        } catch (final NoSuchEntityException e) {
            try {
                entity = Optional.of(doCreatePublication(publication));
            } catch (final Exception e1) {
                log.log(Level.SEVERE, "failed to create publication", e);
                exception = Optional.of(e);
            }
        } catch (final Exception e) {
            // should never get here
            log.log(Level.SEVERE, "caught unknown error", e);
            exception = Optional.of(e);
        }

        assert (exception.isPresent() && !entity.isPresent()) || (!exception.isPresent() && entity.isPresent())
                : String.format("wrong assertion - %s, %s", exception, entity);

        if (exception.isPresent()) {
            //log.log(Level.INFO, "Can't save the publication", exception.get());
            PublicationServiceImpl.notifyExceptionIfNotNull(callback, exception.get());

        } else {
            val entityVal = entity.get();

            //log.log(Level.INFO, String.format("Publication entity %s", entityVal));

            if (needUpload(entityVal, new URL(publication.getFile_link()))) {
                //log.log(Level.INFO, "Insert publication for upload");
                storageService.uploadPaper(entityVal, new ResultCallback<File>() {

                    @Override
                    public void onResult(@NotNull File file) {
                        entityVal.setUploadStatus(UploadStatus.UPLOADED);
                        entityVal.setContentLength(file.length());

                        try {
                            updatePublication(entityVal);
                            PublicationServiceImpl.notifyIfNotNull(callback, entityVal);
                        } catch (final Exception e) {
                            PublicationServiceImpl.notifyExceptionIfNotNull(callback, e);
                        }
                    }

                    @Override
                    public void onException(@NotNull Exception e) {
                        entityVal.setUploadStatus(UploadStatus.FAILED);

                        try {
                            updatePublication(entityVal);
                            PublicationServiceImpl.notifyExceptionIfNotNull(callback, new ServiceErrorException(e));
                        } catch (final ValidationException | ServiceErrorException e1) {
                            PublicationServiceImpl.notifyExceptionIfNotNull(callback, new ServiceErrorException(e));
                        }
                    }
                });
            } else {
                // publication was already indexed, proceed
                //log.log(Level.INFO, "Publication was already processed, skipping");
                PublicationServiceImpl.notifyIfNotNull(callback, entityVal);
            }
        }
    }

    private PublicationEntity doCreatePublication(PublicationView view) throws ValidationException, NoSuchEntityException, ServiceErrorException {
        PublicationEntity entity = new PublicationEntity();
        merge(entity, view);
        addAuthors(entity, view);
        entity.setUploadStatus(UploadStatus.PENDING);
        publicationValidateService.publicationValidForCreation(entity);
        entity = publicationRepository.saveAndFlush(entity);
        if (entity == null) {
            throw new ServiceErrorException();
        }
        return entity;
    }

    private static PublicationCriteria newCriteriaForPublication(PublicationView publication) {
        PublicationCriteria criteria;

        try {
            criteria = new PublicationCriteria("{}");
        } catch (final WrongRestrictionException e) {
            throw new RuntimeException(e);
        }

        criteria.setLink(publication.getLink());
        criteria.setOffset(0);
        criteria.setLimit(2);

        return criteria;
    }

    private static <T> void notifyIfNotNull(ResultCallback<T> callback, T t) {
        Preconditions.checkNotNull(t);

        if (callback != null) {
            callback.onResult(t);
        }
    }

    private static void notifyExceptionIfNotNull(@Nullable ResultCallback<?> callback, @NotNull Exception e) {
        Preconditions.checkNotNull(e);

        if (callback != null) {
            callback.onException(e);
        }
    }

    private static boolean needUpload(PublicationEntity entity, URL url) {
        return entity.getUploadStatus() != UploadStatus.UPLOADED || UrlUtils.checkContentUpdated(url, entity);
    }

}
