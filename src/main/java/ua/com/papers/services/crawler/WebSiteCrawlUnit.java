package ua.com.papers.services.crawler;

import lombok.Value;
import ua.com.papers.crawler.core.creator.ICrawlerManagerFactory;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;

import java.util.Collection;

/**
 * <p>
 *     Describes web site crawling unit
 * </p>
 * Created by Максим on 12/10/2017.
 */
@Value
public class WebSiteCrawlUnit {
    ICrawlerManager manager;
    Collection<Object> handlers;

    public WebSiteCrawlUnit(ICrawlerManagerFactory factory, Collection<Object> handlers) {
        this.manager = factory.create();
        this.handlers = handlers;
    }

}
