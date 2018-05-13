package ua.com.papers.services.crawler.unit.nbuv;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v2.PageHandler;
import ua.com.papers.crawler.settings.v2.analyze.ContentAnalyzer;
import ua.com.papers.crawler.settings.v2.process.AfterPage;
import ua.com.papers.crawler.settings.v2.process.BeforePage;
import ua.com.papers.crawler.settings.v2.process.Binding;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.crawler.BasePublicationHandler;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.utils.ResultCallback;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/10/2017.
 */
@Log
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@PageHandler(
        minWeight = 80,
        analyzers = {
                // Has a file link
                @ContentAnalyzer(weight = 20, selector = "#aspect_artifactbrowser_ItemViewer_div_item-view > div > div > div.file-link > a"),
                // Has authors
                @ContentAnalyzer(weight = 20, selector = "#aspect_artifactbrowser_ItemViewer_div_item-view > table > tbody > tr > td.label-cell:containsOwn(dc.contributor.author)"),
                // Has publishers
                @ContentAnalyzer(weight = 20, selector = "#aspect_artifactbrowser_ItemViewer_div_item-view > table > tbody > tr > td.label-cell:containsOwn(dc.publisher)"),
                // Has title
                @ContentAnalyzer(weight = 20, selector = "#aspect_artifactbrowser_ItemViewer_div_item-view > table > tbody > tr > td.label-cell:containsOwn(dc.title)")
        }
)
public final class NbuvArticleHandler extends BasePublicationHandler {

    IPublisherService publisherService;
    IPublicationService publicationService;

    @NonFinal
    Map<String, Integer> titleToId;
    Map<String, Integer> fullNameToId = new HashMap<>();

    @Autowired
    public NbuvArticleHandler(IAuthorService authorService, IPublisherService publisherService, IPublicationService publicationService) {
        super(authorService);
        this.publisherService = publisherService;
        this.publicationService = publicationService;
    }

    @BeforePage
    public void onPrepare(ua.com.papers.crawler.core.main.bo.Page page) throws WrongRestrictionException {
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
    }

    @AfterPage
    public void onPageParsed(ua.com.papers.crawler.core.main.bo.Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));
    }

    public void onHandleArticle(
            @NotNull @Binding(selectors = "#aspect_artifactbrowser_ItemViewer_div_item-view > div > div:nth-child(1) > div.file-link > a") URL link,
            @NotNull @Binding(selectors = "#aspect_artifactbrowser_ItemViewer_div_item-view > table > tbody > tr:has(td.label-cell:containsOwn(dc.contributor.author)) > td:nth-child(2)") Collection<Element> authors,
            @NotNull @Binding(selectors = "#aspect_artifactbrowser_ItemViewer_div_item-view > table > tbody > tr:has(td.label-cell:containsOwn(dc.publisher)) > td:nth-child(2)") Element publisher,
            @NotNull @Binding(selectors = "#aspect_artifactbrowser_ItemViewer_div_item-view > table > tbody > tr:has(td.label-cell:matchesOwn(dc.title\\z)) > td:nth-child(2)") Element title,
            Page page) throws Exception {

        log.log(Level.INFO, String.format("onHandleArticle, url=%s, authors=%s, publisher=%s, title=%s", link, authors, publisher.text(), title.ownText()));

        val publicationView = new PublicationView();

        publicationView.setLink(page.getUrl().toExternalForm());
        publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        publicationView.setType(PublicationTypeEnum.ARTICLE);
        publicationView.setFile_link(link.toExternalForm());

        val ids = authors.stream().map(Element::ownText).map(this::findAuthorIdByName)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

        publicationView.setAuthors_id(ids);

        if (TextUtils.isEmpty(publisher.ownText())) {
            log.log(Level.WARNING, String.format("failed to parse title, %s", publisher));
        } else {
            publicationView.setPublisher_id(preparePublisher(publisher.text().trim()).getId());
        }

        publicationView.setTitle(title.ownText().trim());

        if (publicationView.isValid()) {
            log.log(Level.INFO, String.format("trying to save publication %s", publicationView));

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
            log.log(Level.WARNING, String.format("failed to process publication, publication view=%s", publicationView));
        }
    }

    @NonNull
    private PublisherView preparePublisher(String title) throws Exception {
        Preconditions.checkArgument(!title.isEmpty(), "Empty publisher title");

        val publisherView = new PublisherView();

        publisherView.setTitle(title);

        var id = titleToId.get(title);

        if (id == null) {
            log.log(Level.INFO, "Title" + publisherView.getTitle());
            val entity = publisherService.findPublisherByTitle(publisherView.getTitle());

            if (entity == null) {
                id = publisherService.createPublisher(publisherView);
            } else {
                id = entity.getId();
            }
            titleToId.put(publisherView.getTitle(), id);
        }

        publisherView.setId(id);

        return publisherView;
    }

    @SneakyThrows
    private Optional<Integer> findAuthorIdByName(String fullName) {
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
