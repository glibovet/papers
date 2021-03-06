package ua.com.papers.services.crawler.unit.uran;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.settings.v2.PageHandler;
import ua.com.papers.crawler.settings.v2.analyze.ContentAnalyzer;
import ua.com.papers.crawler.settings.v2.process.AfterPage;
import ua.com.papers.crawler.settings.v2.process.BeforePage;
import ua.com.papers.crawler.settings.v2.process.Binding;
import ua.com.papers.crawler.settings.v2.process.Handles;
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
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Collects url to process
 */
@Log
@Component
@PageHandler(
        analyzers = {
                @ContentAnalyzer(weight = 30, selector = "#content > table > tbody > tr:nth-child(1) > td.tocGalleys > a"),
                @ContentAnalyzer(weight = 30, selector = "#content > table > tbody > tr:nth-child(1) > td.tocTitle > a"),
                @ContentAnalyzer(weight = 30, selector = "#content > table > tbody > tr:nth-child(2) > td.tocAuthors")
        })
public final class UranArticleHandler extends BasePublicationHandler {

    private static final int PUBLICATION_GROUP = 1;
    private static final Pattern FULL_NAME_PATTERN = Pattern.compile("[(\\[].*?[)\\]]");

    private final IPublisherService publisherService;
    private final IPublicationService publicationService;

    private final PublicationView publicationView = new PublicationView();
    private PublisherView publisherView = new PublisherView();
    private Map<String, Integer> titleToId;
    private Map<String, Integer> fullNameToId = new HashMap<>();

    private final Collection<PublicationView> publicationViews = new ArrayList<>();

    private final SlackChannel slackChannel;
    private final SlackSession slackSession;

    @Autowired
    public UranArticleHandler(IAuthorService authorService, IPublisherService publisherService,
                              IPublicationService publicationService, Handler handler,
                              SlackChannel crawlerChannel, SlackSession slackSession) {
        super(authorService);
        this.publisherService = publisherService;
        this.publicationService = publicationService;
        this.slackChannel = crawlerChannel;
        this.slackSession = slackSession;

        publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        publicationView.setType(PublicationTypeEnum.ARTICLE);

        log.addHandler(handler);
    }

    @BeforePage
    public void onPrepare(ua.com.papers.crawler.core.main.model.Page page) throws WrongRestrictionException {
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
        publicationView.reset();
        publisherView.reset();
        publicationViews.clear();
    }

    @AfterPage
    public void onPageParsed(Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));

        for (val view : publicationViews) {

            view.setPublisher_id(publisherView.getId());

            if (view.isValid()) {
                upload(view);
            } else {
                log.log(Level.WARNING, String.format("failed to process publication=%s", view));
            }
        }
    }

    // specify prefix to fetch DOM elements from same parent element (here parent is #content > table > tbody)
    @Handles(
            selectors = "#content > table > tbody"
    )
    public void onHandleArticle(
            // @Converts annotation is optional; acceptable parameter type adapter will be searched among registered
            // adapters
            @NotNull @Binding(selectors = "tr:nth-child(1) > td.tocGalleys > a[href*='article/view']:first-child") URL link,
            // handler respects @NotNull annotations
            @NotNull @Binding(selectors = "tr:nth-child(1) > td.tocTitle > a") String title,
            @NotNull @Binding(selectors = "tr:nth-child(2) > td.tocAuthors") String authors,
            Page page) {

        assert page != null : "page == null";
        assert link != null : "link == null, page=" + page.getUrl();
        assert title != null : "title == null, page=" + page.getUrl();
        assert authors != null : "authors == null, page=" + page.getUrl();

        log.log(Level.INFO, String.format("onHandleArticle# link=%s, title=%s, authors=%s, page=%s", link, title, authors, page.getUrl()));

        val publicationView = new PublicationView();
        var strLink = link.toExternalForm();

        if (strLink.contains("view")) {
            strLink = strLink.replace("view", "download");
        }

        publicationView.setFile_link(strLink);
        publicationView.setTitle(title);

        val ids = Arrays.stream(authors.replaceAll(FULL_NAME_PATTERN.pattern(), "").split(","))
                .map(fullName -> findAuthorIdByName(fullName.trim()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        publicationView.setAuthors_id(ids);
        publicationView.setLink(page.getUrl().toExternalForm());

        publicationViews.add(publicationView);
    }

    @Handles(
            group = PUBLICATION_GROUP,
            selectors = "#headerTitle > h1"
    )
    public void onHandlePublisher(String publisher) {
        log.log(Level.INFO, String.format("#onHandlePublisher %s, %s", getClass(), publisher));

        publisherView = createPublisherView(publisher);
    }

    private Optional<Integer> findAuthorIdByName(String fullName) {
        var id = fullNameToId.get(fullName);

        if (id != null) {
            return Optional.of(id);
        }

        val parts = Arrays.stream(fullName.split(" ")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        try {
            if (parts.size() == 2) {
                // name + last name
                id = findAuthorId(TextUtils.formatInitials(parts.get(0)), TextUtils.capitalize(parts.get(1)), TextUtils.formatName(parts.get(0), parts.get(1)));
            } else if (parts.size() >= 3) {
                // last name + first name + middle name
                id = findAuthorId(TextUtils.formatInitials(parts.get(1), parts.get(2)), TextUtils.capitalize(parts.get(0)), TextUtils.formatName(parts.get(1), parts.get(2), parts.get(0)));
            }
        } catch (final Throwable th) {
            log.log(Level.WARNING, String.format("Couldn't process full name %s", fullName), th);
            return Optional.empty();
        }


        fullNameToId.put(fullName, id);
        return Optional.ofNullable(id);
    }

    private PublisherView createPublisherView(String publisher) {
        val publisherView = new PublisherView();

        publisherView.setTitle(publisher);

        Integer id = titleToId.get(publisherView.getTitle());

        if (id == null) {

            try {
                val entity = publisherService.findPublisherByTitle(publisherView.getTitle());

                if (entity == null) {
                    id = publisherService.createPublisher(publisherView);
                } else {
                    id = entity.getId();
                }
                titleToId.put(publisherView.getTitle(), id);
            } catch (final Exception e) {
                log.log(Level.WARNING, String.format("failed to create publisher Uran, title %s",
                        publisherView.getTitle()), e);
                throw new RuntimeException(e);
            }
        }

        publisherView.setId(id);
        log.log(Level.INFO, String.format("publisher was proceeded successfully %s", publisherView.getTitle()));

        return publisherView;
    }

    private void upload(PublicationView publicationView) {
        log.log(Level.INFO, String.format("trying to save publication %s", publicationView));

        publicationService.savePublicationFromRobot(publicationView, new ResultCallback<PublicationEntity>() {
            @Override
            public void onResult(@NotNull PublicationEntity publicationEntity) {
                val message = String.format("Publication %s was saved %s", publicationEntity.getLink(), publicationEntity.getFileLink());

                log.log(Level.INFO, message);
                slackSession.sendMessage(slackChannel, message);
            }

            @Override
            public void onException(@NotNull Exception e) {
                log.log(Level.WARNING, String.format("Failed to save publication %s", publicationView.getLink()), e);
            }
        });
    }

}
