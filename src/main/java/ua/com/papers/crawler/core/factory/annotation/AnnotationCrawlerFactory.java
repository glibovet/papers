package ua.com.papers.crawler.core.factory.annotation;

import lombok.NonNull;
import ua.com.papers.crawler.core.analyze.Analyzer;
import ua.com.papers.crawler.core.factory.CrawlerFactory;
import ua.com.papers.crawler.core.main.Crawler;
import ua.com.papers.crawler.core.main.CrawlerContext;
import ua.com.papers.crawler.core.processor.FormatterFactory;
import ua.com.papers.crawler.core.processor.annotation.AnnotationFormatterFactoryImp;
import ua.com.papers.crawler.core.processor.annotation.processor.PageSettingsProcessor;
import ua.com.papers.crawler.core.select.UrlExtractor;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.JobId;
import ua.com.papers.crawler.settings.SchedulerSetting;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Set;

/**
 * {@linkplain CrawlerFactory} skeleton realization. Can be used as base for annotation based
 * crawler factories
 */
public abstract class AnnotationCrawlerFactory implements CrawlerFactory {
    private final Settings setting;
    private final Set<?> handlers;

    public AnnotationCrawlerFactory(@NonNull JobId jobId,
                                    @NonNull SchedulerSetting schedulerSetting,
                                    @NonNull Set<? extends URL> startUrls,
                                    @NonNull Set<?> handlers) {
        this.handlers = handlers;
        this.setting = new Settings(jobId, schedulerSetting, startUrls, new PageSettingsProcessor(handlers).process());
    }

    @Override
    public final Crawler create() {
        return new CrawlerContext(createAnalyzeManager(setting), setting, createUrlExtractor(setting),
                createFormatFactory(setting).create(handlers), createUrlsRepository(setting));
    }

    protected FormatterFactory createFormatFactory(@NotNull Settings settings) {
        return new AnnotationFormatterFactoryImp();
    }

    protected abstract UrlExtractor createUrlExtractor(@NotNull Settings settings);

    protected abstract Analyzer createAnalyzeManager(@NotNull Settings settings);

    protected abstract UrlsRepository createUrlsRepository(@NotNull Settings settings);
}
