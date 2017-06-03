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
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;

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
    IHandlerCallback callback;
    List<PublicationView> publicationViews;

    @NonFinal
    private boolean isFailed;
    @NonFinal
    private PublisherView publisherView;

    public UranArticleComposer(IAuthorService authorService, IPublisherService publisherService,
                               IPublicationService publicationService) {
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.publicationService = publicationService;
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

                publication.setPublisherId(publisherView.getId());
                publicationService.createPublication(publication);

                log.log(Level.INFO, String.format("page with url %s was successfully saved", page.getUrl()));
            } catch (final ServiceErrorException | NoSuchEntityException e) {
                log.log(Level.WARNING, "Service error occurred while saving publication", e);
            } catch (final ValidationException e) {
                log.log(Level.SEVERE,
                        String.format("Fatal error occurred while saving publication, cause: publication %s, publisher %s",
                                publication, publisherView),
                        e);
                // finish execution immediately and fix error
                throw new RuntimeException(e);
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
