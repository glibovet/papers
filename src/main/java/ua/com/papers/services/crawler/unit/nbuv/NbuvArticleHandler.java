package ua.com.papers.services.crawler.unit.nbuv;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.util.*;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.crawler.UrlAdapter;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/10/2017.
 */
@PageHandler(id = 2)
@Log
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public final class NbuvArticleHandler {

    IAuthorService authorService;
    IPublisherService publisherService;
    IPublicationService publicationService;

    @NonFinal
    PublicationView publicationView;
    @NonFinal
    PublisherView publisherView;
    @NonFinal
    Map<String, Integer> titleToId;

    @Autowired
    public NbuvArticleHandler(IAuthorService authorService, IPublisherService publisherService, IPublicationService publicationService) {
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.publicationService = publicationService;
    }

    @PreHandle
    public void onPrepare(Page page) throws WrongRestrictionException, NoSuchEntityException {
        log.log(Level.INFO, String.format("#onPrepare %s, %s", getClass(), page.getUrl()));

        publicationView = new PublicationView();
        publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        publicationView.setType(PublicationTypeEnum.ARTICLE);

        if (titleToId == null) {
            // load all data
            try {
                titleToId = publisherService.getPublishers(0, -1, null)
                        .stream()
                        .collect(Collectors.toMap(PublisherEntity::getTitle, PublisherEntity::getId));
            } catch (final NoSuchEntityException e) {//FIXME if db is empty...OMG, whyyyy
                log.log(Level.WARNING, "FIXME", e);
                titleToId = new HashMap<>();
            }
        }
        // reset instance
        publisherView.setId(null);
        publisherView.setTitle(null);
    }

    @PostHandle
    public void onPageParsed(Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));
        // save parsed page link
        /*publicationView.setLink(page.getUrl().toExternalForm());

        val isValid = !TextUtils.isEmpty(publicationView.getLink())
                && !TextUtils.isEmpty(publicationView.getTitle())
                && publicationView.getAuthors_id() != null && !publicationView.getAuthors_id().isEmpty();

        if (isValid) {
            log.log(Level.INFO, String.format("trying to save publication %s", publicationView.getLink()));

            publicationView.setPublisher_id(publisherView.getId());
            publicationService.savePublicationFromRobot(publicationView, new ResultCallback<PublicationEntity>() {
                @Override
                public void onResult(@NotNull PublicationEntity publicationEntity) {
                    log.log(Level.INFO, String.format("Publication %s with url %s was saved", publicationEntity.getLink(), publicationEntity.getFileLink()));
                }

                @Override
                public void onException(@NotNull Exception e) {
                    log.log(Level.WARNING, String.format("Failed to save publication %s", publicationView.getLink()), e);
                }
            });
        } else {
            log.log(Level.WARNING, "failed to process publication");
        }*/

        preparePublisher(publisherView);
    }

    @Handler(id = 6, converter = UrlAdapter.class)
    public void onHandleUri(URL link) {
        log.log(Level.INFO, "onHandleUri " + link);
    }

    @Handler(id = 7)
    public void onHandleAuthors(Element authors) {
        log("onHandleAuthors", authors);
    }

    @Handler(id = 8)
    public void onHandlePublishers(String publishers) {
        log.log(Level.INFO, "onHandlePublishers " + publishers);

        if (TextUtils.isEmpty(publishers)) {
            log.log(Level.WARNING, String.format("failed to parse title, %s", getClass()));
        } else {
            publisherView.setTitle(publishers.replaceAll("[\\[\\],]", ""));
        }
    }

    @Handler(id = 9)
    public void onHandleTitle(String title) {
        log.log(Level.INFO, "onHandleTitle " + title);
        publicationView.setTitle(title);
    }

    private Optional<? extends PublisherView> preparePublisher(PublisherView publisherView) throws Exception {
        if (TextUtils.isEmpty(publisherView.getTitle())) {
            return Optional.empty();
        }
        var id = titleToId.get(publisherView.getTitle());

        if (id == null) {
            val entity = publisherService.findPublisherByTitle(publisherView.getTitle());

            if (entity == null) {
                id = publisherService.createPublisher(publisherView);
            } else {
                id = entity.getId();
            }
            titleToId.put(publisherView.getTitle(), id);
        }

        publisherView.setId(id);
        return Optional.of(publisherView);
    }

    private static void log(String method, Element element) {
        log.log(Level.INFO, method + " " + element);
    }

}
