package ua.com.papers.services.crawler.unit.uran;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.services.crawler.IHandlerCallback;
import ua.com.papers.crawler.settings.v1.PageHandlerV1;
import ua.com.papers.crawler.settings.v1.PostHandle;
import ua.com.papers.crawler.settings.v1.PreHandle;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.storage.IStorageService;
import ua.com.papers.utils.ResultCallback;

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
@PageHandlerV1(id = 2)
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
            publication.setPublisher_id(publisherView.getId());
            if (publication.getLink()!=null&&publication.getFile_link()==null) {
                String file_url = publication.getLink();
                if (!file_url.contains("viewIssue")) {
                    if (file_url.contains("view"))
                        file_url = file_url.replace("view", "download");
                    publication.setFile_link(file_url);
                }
            }
            publicationService.savePublicationFromRobot(publication, new ResultCallback<PublicationEntity>() {
                @Override
                public void onResult(@NotNull PublicationEntity publicationEntity) {
                    log.log(Level.INFO, String.format("Publication %s with url %s was saved", publicationEntity.getLink(), publicationEntity.getFileLink()));
                }

                @Override
                public void onException(@NotNull Exception e) {
                    log.log(Level.WARNING, String.format("Failed to save publication %s", publication.getLink()), e);
                }
            });
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
