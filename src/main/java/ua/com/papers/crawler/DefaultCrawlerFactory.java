package ua.com.papers.crawler;

import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.util.ICrawlerFactory;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 11/27/2016.
 */
public final class DefaultCrawlerFactory implements ICrawlerFactory {

    @Override
    public ICrawler create(@NotNull Settings settings) {



        return null;
    }
}
