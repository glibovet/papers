package ua.com.papers.services.crawler;

import lombok.NonNull;
import ua.com.papers.crawler.core.analyze.Analyzer;
import ua.com.papers.crawler.core.analyze.AnalyzerImp;
import ua.com.papers.crawler.core.analyze.PageAnalyzer;
import ua.com.papers.crawler.core.analyze.PageAnalyzerImp;
import ua.com.papers.crawler.core.factory.annotation.AnnotationCrawlerFactory;
import ua.com.papers.crawler.core.select.UrlExtractor;
import ua.com.papers.crawler.core.select.UrlExtractorImp;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.JobId;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.SchedulerSetting;
import ua.com.papers.crawler.settings.Settings;
import ua.com.papers.services.crawler.unit.repo.JpaUrlsRepository;
import ua.com.papers.services.crawler.unit.repo.JpaUrlsRepositoryImp;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class AnnotationCrawlerFactoryImp extends AnnotationCrawlerFactory {
    private final JpaUrlsRepository urlsRepository;

    public AnnotationCrawlerFactoryImp(@NonNull JobId jobId,
                                       @NonNull SchedulerSetting schedulerSetting,
                                       @NonNull Set<URL> startUrls,
                                       @NonNull Set<Object> handlers,
                                       @NonNull JpaUrlsRepository urlsRepository) {
        super(jobId, schedulerSetting, startUrls, handlers);
        this.urlsRepository = urlsRepository;
    }

    protected UrlExtractor createUrlExtractor(@NotNull Settings settings) {
        return new UrlExtractorImp(settings.getPageSettings()
                .stream()
                .collect(Collectors
                        .toMap(PageSetting::getId,
                                s -> s.getSelectSettings().isEmpty() ? Collections.emptyList() : s.getSelectSettings())
                )
        );
    }

    protected Analyzer createAnalyzeManager(@NotNull Settings settings) {
        return new AnalyzerImp(settings.getPageSettings().stream().map(this::createPageAnalyzer).collect(Collectors.toList()));
    }

    protected UrlsRepository createUrlsRepository(@NotNull Settings settings) {
        return new JpaUrlsRepositoryImp(urlsRepository);
    }

    private PageAnalyzer createPageAnalyzer(PageSetting setting) {
        return new PageAnalyzerImp(setting.getMinWeight(), setting.getId(), setting.getAnalyzeTemplates());
    }

}
