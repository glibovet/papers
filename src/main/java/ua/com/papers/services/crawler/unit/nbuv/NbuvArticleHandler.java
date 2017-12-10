package ua.com.papers.services.crawler.unit.nbuv;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.util.Handler;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;

import java.util.logging.Level;

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

    @Autowired
    public NbuvArticleHandler(IAuthorService authorService, IPublisherService publisherService, IPublicationService publicationService) {
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.publicationService = publicationService;
    }

    @PreHandle
    public void onPrepare(Page page) {
        log.log(Level.INFO, String.format("#onPrepare %s, %s", getClass(), page.getUrl()));
    }

    @PostHandle
    public void onPageParsed(Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));
    }

    @Handler(id = 6)
    public void onHandleUri(Element href) {
        log("onHandleUri", href);
    }

    @Handler(id = 7)
    public void onHandleAuthors(Element authors) {
        log("onHandleAuthors", authors);
    }

    @Handler(id = 8)
    public void onHandlePublishers(Element publishers) {
        log("onHandlePublishers", publishers);
    }

    @Handler(id = 9)
    public void onHandleTitle(Element title) {
        log("onHandleTitle", title);
    }

    private static void log(String method, Element element) {
        log.log(Level.INFO, method + " " + element);
    }

}
