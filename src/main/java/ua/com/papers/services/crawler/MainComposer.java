package ua.com.papers.services.crawler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.services.crawler.unit.ukma.UkmaArticleComposer;
import ua.com.papers.services.crawler.unit.uran.UranArticleComposer;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.storage.IStorageService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * <p>
 *     Delegates page handlers creation to sub composers,
 *     the main reason of creation of this class is that
 *     collection {@linkplain AuthorEntity} should be provided to
 *     author sub handlers in order to initialize their caches,
 *     which is the crutch
 * </p>
 * Created by Максим on 2/8/2017.
 */
@Log
@Value
@Getter(AccessLevel.NONE)
@Service
public final class MainComposer {

    IAuthorService authorService;
    IPublisherService publisherService;
    IPublicationService publicationService;
    IStorageService storageService;
    @NonFinal volatile Collection<Object> subHandlers;

    @NonFinal
    private boolean isFailed;
    @NonFinal
    private PublisherView publisherView;

    @Autowired
    public MainComposer(IAuthorService authorService, IPublisherService publisherService,
                        IPublicationService publicationService, IStorageService storageService) {
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.publicationService = publicationService;
        this.storageService = storageService;
    }

    /**
     * Note that this method may hang the system on the
     * first initialization
     *
     * @return page handlers
     */
    public Collection<Object> asHandlers() {
        // double check approach
        Collection<Object> local = subHandlers;

        if (local == null) {

            List<AuthorEntity> authorEntities;

            try {
                authorEntities = authorService.getAuthors(0, -1, null);
            } catch (final Exception e) {
                authorEntities = new ArrayList<>();
                log.log(Level.SEVERE, "Failed to get authors from db", e);
            }

            local = new ArrayList<>(new UkmaArticleComposer(authorService, publisherService, publicationService, storageService)
                    .asHandlers(authorEntities));
            local.addAll(new UranArticleComposer(authorService, publisherService, publicationService, storageService)
                    .asHandlers(authorEntities));
            // references assignment is atomic, so
            // we don't need to use lock
            subHandlers = local;
        }

        return local;
    }

}
