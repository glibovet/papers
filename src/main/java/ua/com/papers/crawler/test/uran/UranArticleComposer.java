package ua.com.papers.crawler.test.uran;

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
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.*;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
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
@PageHandler(id = 2)
public final class UranArticleComposer {

    IAuthorService authorService;
    IPublisherService publisherService;
    IPublicationService publicationService;
    IStorageService storageService;
    IHandlerCallback callback;
    List<PublicationView> publicationViews;

    @NonFinal
    private boolean isFailed;
    @NonFinal
    private PublisherView publisherView;

    public UranArticleComposer(IAuthorService authorService, IPublisherService publisherService,
                               IPublicationService publicationService, IStorageService storageService) {
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.publicationService = publicationService;
        this.publicationViews = new ArrayList<>();
        this.storageService = storageService;
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
                if (publication.getLink()!=null&&publication.getFile_link()==null) {
                    String file_url = publication.getLink();
                    if (!file_url.contains("viewIssue")) {
                        if (file_url.contains("view"))
                            file_url = file_url.replace("view", "download");
                        publication.setFile_link(file_url);
                    }
                }
                publicationService.savePublicationFromRobot(publication);

            } catch (WrongRestrictionException | NoSuchEntityException e) {}
            catch (ElasticSearchException elasticSearchException) {
                log.log(Level.SEVERE, "Fatal error occurred while saving publication Uran. Problem with Elastic", elasticSearchException);
            } catch (ForbiddenException e) {
                log.log(Level.WARNING, "Service error occurred while saving publication Uran", e);
            } catch (ValidationException e) {
                log.log(Level.SEVERE, "Fatal error occurred while saving publication Uran", e);
            } catch (ServiceErrorException e) {
                log.log(Level.SEVERE, "Fatal error occurred while saving publication Uran", e);
            }
        }
    }

    public Collection<Object> asHandlers(List<AuthorEntity> authorEntities) {
        return Arrays.asList(
                new UranPublicationHandler(authorService, callback, authorEntities),
                new UranPublisherHandler(publisherService, callback),
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
                UranArticleComposer.this.publisherView = publisherView;
            }

            @Override
            public void onPublicationReady(@NotNull PublicationView publicationView) {
                UranArticleComposer.this.publicationViews.add(publicationView);
            }
        };
    }

}
