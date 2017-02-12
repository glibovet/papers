package ua.com.papers.crawler.test;

import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.creator.SimpleCrawlerFactory;
import ua.com.papers.crawler.core.domain.Crawler;
import ua.com.papers.crawler.core.domain.ICrawlerPredicate;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.PageIndexer;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.storage.IPageIndexRepository;
import ua.com.papers.crawler.settings.Settings;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 2/12/2017.
 */
@Component
@Value
public class AppCrawlerFactory extends SimpleCrawlerFactory {

    IPageIndexRepository repository;

    @Autowired
    public AppCrawlerFactory(IPageIndexRepository repository) {
        this.repository = repository;
    }

    @Nullable
    @Override
    protected ICrawlerPredicate createRunPredicate() {
        return (visitedUrls, acceptedPages) -> Runtime.getRuntime().freeMemory() > Crawler.getMinFreeMemory()
                && visitedUrls.size() <= 100;
    }

    @Override
    protected IPageIndexer createPageIndexer(@NotNull Settings settings, @NotNull IFormatManagerFactory formatFactory,
                                             @NotNull IAnalyzeManager analyzeManager) {
        return new PageIndexer(repository, formatFactory, analyzeManager);
    }
}
