package ua.com.papers.crawler.core.factory.annotation;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.papers.crawler.core.analyze.Analyzer;
import ua.com.papers.crawler.core.factory.ICrawlerFactory;
import ua.com.papers.crawler.core.main.ICrawler;
import ua.com.papers.crawler.core.main.v2.CrawlerV2;
import ua.com.papers.crawler.core.processor.FormatterFactory;
import ua.com.papers.crawler.core.processor.annotation.processor.PageSettingsProcessor;
import ua.com.papers.crawler.core.select.IUrlExtractor;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.JobId;
import ua.com.papers.crawler.settings.SchedulerSetting;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Set;

public abstract class AnnotationCrawlerFactory implements ICrawlerFactory {
    private final Settings setting;
    private final Set<?> handlers;

    @Autowired
    public AnnotationCrawlerFactory(@NonNull JobId jobId,
                                    @NonNull SchedulerSetting schedulerSetting,
                                    @NonNull Set<? extends URL> startUrls,
                                    @NonNull Set<?> handlers) {
        this.handlers = handlers;
        this.setting = new Settings(jobId, schedulerSetting, startUrls, new PageSettingsProcessor(handlers).process());
    }

    @Override
    public final ICrawler create() {
        return new CrawlerV2(createAnalyzeManager(setting), setting, createUrlExtractor(setting),
                createFormatFactory(setting).create(handlers), createUrlsRepository(setting));
    }

    protected abstract FormatterFactory createFormatFactory(@NotNull Settings settings);

    protected abstract IUrlExtractor createUrlExtractor(@NotNull Settings settings);

    protected abstract Analyzer createAnalyzeManager(@NotNull Settings settings);

    protected abstract UrlsRepository createUrlsRepository(@NotNull Settings settings);
}
