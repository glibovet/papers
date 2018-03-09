package ua.com.papers.crawler.core.factory;

import lombok.val;
import ua.com.papers.crawler.core.domain.Crawler;
import ua.com.papers.crawler.core.domain.ICrawlerPredicate;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.processor.IFormatManagerFactory;
import ua.com.papers.crawler.core.schedule.CrawlerManager;
import ua.com.papers.crawler.core.schedule.ICrawlerManager;
import ua.com.papers.crawler.core.select.IUrlExtractor;
import ua.com.papers.crawler.settings.Settings;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * Skeleton realization of {@linkplain ICrawlerFactory}
 * </p>
 * Created by Максим on 1/17/2017.
 */
public abstract class AbstractCrawlerFactory implements ICrawlerFactory {

    protected AbstractCrawlerFactory() {
    }

    /**
     * Skeleton realization, may be overridden by subclasses if another
     * behaviour required
     *
     * @param settings settings to be used while creating crawler
     * @return new instance of crawler scheduler
     * @see ICrawlerFactory
     */
    @Override
    public ICrawlerManager create(@NotNull Settings settings) {

        val analyzeManager = createAnalyzeManager(settings);
        val formatFactory = createFormatFactory(settings);
        val crawler = Crawler.builder()
                .schedulerSetting(settings.getSchedulerSetting())
                .analyzeManager(analyzeManager)
                .formatManagerFactory(formatFactory)
                .urlExtractor(createUrlExtractor(settings))
                .predicate(createRunPredicate())
                .build();

        return CrawlerManager.builder()
                .crawler(crawler)
                .startUrls(settings.getStartUrls())
                .indexer(createPageIndexer(settings, formatFactory, analyzeManager))
                .build();
    }

    // implement methods to supply your own behaviour

    @Nullable
    protected abstract ICrawlerPredicate createRunPredicate();

    protected abstract IFormatManagerFactory createFormatFactory(@NotNull Settings settings);

    protected abstract IUrlExtractor createUrlExtractor(@NotNull Settings settings);

    protected abstract IAnalyzeManager createAnalyzeManager(@NotNull Settings settings);

    protected abstract IPageIndexer createPageIndexer(@NotNull Settings settings,
                                                      @NotNull IFormatManagerFactory formatFactory,
                                                      @NotNull IAnalyzeManager analyzeManager);

}
