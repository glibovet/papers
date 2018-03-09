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
import ua.com.papers.crawler.core.processor.xml.annotation.Part;
import ua.com.papers.crawler.core.processor.xml.annotation.PageHandler;
import ua.com.papers.crawler.core.processor.xml.annotation.PostHandle;
import ua.com.papers.crawler.core.processor.xml.annotation.PreHandle;
import ua.com.papers.crawler.util.*;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.crawler.BasePublicationHandler;
import ua.com.papers.services.crawler.UrlAdapter;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.utils.ResultCallback;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/10/2017.
 */
@PageHandler(id = 2)
@Log
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public final class NbuvArticleHandler extends BasePublicationHandler {

    IPublisherService publisherService;
    IPublicationService publicationService;

    PublicationView publicationView = new PublicationView();
    PublisherView publisherView = new PublisherView();
    @NonFinal
    Map<String, Integer> titleToId;
    Map<String, Integer> fullNameToId = new HashMap<>();

    @Autowired
    public NbuvArticleHandler(IAuthorService authorService, IPublisherService publisherService, IPublicationService publicationService) {
        super(authorService);
        this.publisherService = publisherService;
        this.publicationService = publicationService;

        publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        publicationView.setType(PublicationTypeEnum.ARTICLE);
    }

    @PreHandle
    public void onPrepare(Page page) throws WrongRestrictionException {
        log.log(Level.INFO, String.format("#onPrepare %s, %s", getClass(), page.getUrl()));

        if (titleToId == null) {
            // load all data
            try {
                titleToId = publisherService.getPublishers(0, -1, null)
                        .stream()
                        .collect(Collectors.toMap(PublisherEntity::getTitle, PublisherEntity::getId));
            } catch (final NoSuchEntityException e) {//FIXME if db is empty
                log.log(Level.WARNING, "FIXME", e);
                titleToId = new HashMap<>();
            }
        }
        // reset view instances
        publicationView.setId(null);
        publicationView.setPublisher_id(null);
        publicationView.setLink(null);
        publicationView.setAuthors_id(null);
        publicationView.setTitle(null);
        publicationView.setFile_link(null);

        publisherView.setId(null);
        publisherView.setTitle(null);
    }

    @PostHandle
    public void onPageParsed(Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));
        // save parsed page link
        publicationView.setLink(page.getUrl().toExternalForm());

        val isValid = !TextUtils.isEmpty(publicationView.getLink())
                && !TextUtils.isEmpty(publicationView.getTitle())
                && publicationView.getAuthors_id() != null && !publicationView.getAuthors_id().isEmpty()
                && publisherView.getId() != null;

        if (isValid) {
            log.log(Level.INFO, String.format("trying to save publication %s", publicationView.getLink()));

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
        }
    }

    @Part(id = 6, converter = UrlAdapter.class)
    public void onHandleUri(URL link) {
        log.log(Level.INFO, "onHandleUri " + link);
        publicationView.setFile_link(link.toExternalForm());
    }

    @Part(id = 7)
    public void onHandleAuthors(Element authors) {
        log.log(Level.INFO, "onHandleAuthors " + authors.ownText());

        var ids = publicationView.getAuthors_id();

        if (ids == null) {
            ids = new ArrayList<>();
            publicationView.setAuthors_id(ids);
        }

        try {
            findAuthorIdByName(authors.ownText()).ifPresent(ids::add);
        } catch (final Exception e) {
            log.log(Level.WARNING, "Failed to find author by full name, was " + authors, e);
        }
    }

    @Part(id = 8)
    public void onHandlePublishers(Element publisher) throws Exception {
        log.log(Level.INFO, "onHandlePublisher " + publisher.ownText());

        if (TextUtils.isEmpty(publisher.ownText())) {
            log.log(Level.WARNING, String.format("failed to parse title, %s", publisher));
        } else {
            preparePublisher(publisher.ownText().trim());
            publicationView.setPublisher_id(publisherView.getId());
        }
    }

    @Part(id = 9)
    public void onHandleTitle(Element title) {
        log.log(Level.INFO, "onHandleTitle " + title);
        publicationView.setTitle(title.ownText().trim());
    }

    private void preparePublisher(String title) throws Exception {
        Preconditions.checkArgument(!title.isEmpty(), "Empty publisher title");

        publisherView.setTitle(title);

        var id = titleToId.get(title);

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
    }

    private Optional<Integer> findAuthorIdByName(String fullName) throws NoSuchEntityException, ServiceErrorException, ValidationException {
        var id = fullNameToId.get(fullName);

        if (id != null) {
            return Optional.of(id);
        }

        val credentials = fullName.trim().split(", ");

        if (credentials.length != 2) {
            return Optional.empty();
        }

        val lastName = credentials[0];
        val initials = credentials[1];

        id = findAuthorId(initials, lastName, fullName);

        fullNameToId.put(fullName, id);
        return Optional.of(id);
    }

}
