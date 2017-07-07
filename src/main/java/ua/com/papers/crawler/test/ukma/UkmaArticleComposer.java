package ua.com.papers.crawler.test.ukma;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.test.IHandlerCallback;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;
import ua.com.papers.criteria.impl.PublicationCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.exceptions.service_error.*;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.storage.IStorageService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Максим on 2/8/2017.
 */
@Log
@Value
@Getter(AccessLevel.NONE)
@PageHandler(id = 5)
public final class UkmaArticleComposer {

    IAuthorService authorService;
    IPublisherService publisherService;
    IPublicationService publicationService;
    IHandlerCallback callback;
    List<PublicationView> publicationViews;
    IStorageService storageService;

    @NonFinal
    private boolean isFailed;
    @NonFinal
    private PublisherView publisherView;

    public UkmaArticleComposer(IAuthorService authorService, IPublisherService publisherService,
                               IPublicationService publicationService, IStorageService storageService) {
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.publicationService = publicationService;
        this.storageService = storageService;
        this.publicationViews = new ArrayList<>();
        this.callback = createCallback();
    }

    @PreHandle
    public void onPrepare() {
        // reset variables
        publisherView = null;
        publicationViews.clear();
        isFailed = false;
    }

    @PostHandle
    public void onPageEnd(Page page) {
        // now we can finally store article

        if (isFailed = isFailed || publisherView == null) {
            log.log(Level.SEVERE, "Failed to parse article, either publication " +
                    " or publisher weren't parsed");
            return;
        }

        for (val publication : publicationViews) {
            try {
                publication.setPublisher_id(publisherView.getId());
                publicationService.savePublicationFromRobot(publication);

            } catch (WrongRestrictionException | NoSuchEntityException e) {}
            catch (ElasticSearchError elasticSearchError) {
                log.log(Level.SEVERE, "Fatal error occurred while saving publication UKMA. Problem with Elastic", elasticSearchError);
            } catch (ForbiddenException e) {
                log.log(Level.WARNING, "Service error occurred while saving publication UKMA", e);
            } catch (ValidationException e) {
                log.log(Level.SEVERE, "Fatal error occurred while saving publication UKMA", e);
            } catch (ServiceErrorException e) {
                log.log(Level.SEVERE, "Fatal error occurred while saving publication UKMA", e);
            }

        }
    }

    public Collection<Object> asHandlers(List<AuthorEntity> authorEntities) {
        return Arrays.asList(
                new UkmaPublicationHandler(authorService, callback, authorEntities),
                new UkmaPublisherHandler(publisherService, callback),
                this
        );
    }

    private IHandlerCallback createCallback() {
        return new IHandlerCallback() {

            @Override
            public void onHandleFailure() {
                isFailed = true;
                log.log(Level.SEVERE, "Failed to parse page, no exception passed");
            }

            @Override
            public void onHandleFailure(@NotNull Throwable th) {
                isFailed = true;
                log.log(Level.SEVERE, "Failed to parse page", th);
            }

            @Override
            public void onPublisherReady(@NotNull PublisherView publisherView) {
                UkmaArticleComposer.this.publisherView = publisherView;
            }

            @Override
            public void onPublicationReady(@NotNull PublicationView publicationView) {
                UkmaArticleComposer.this.publicationViews.add(publicationView);
            }
        };
    }

}
