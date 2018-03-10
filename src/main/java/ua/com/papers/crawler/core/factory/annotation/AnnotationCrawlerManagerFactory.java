package ua.com.papers.crawler.core.factory.annotation;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.core.factory.ICrawlerFactory;
import ua.com.papers.crawler.core.factory.ICrawlerManagerFactory;
import ua.com.papers.crawler.core.schedule.ICrawlerManager;
import ua.com.papers.crawler.settings.*;
import ua.com.papers.crawler.settings.v2.Page;
import ua.com.papers.crawler.settings.v2.analyze.UrlAnalyzer;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class AnnotationCrawlerManagerFactory implements ICrawlerManagerFactory {
    private final Set<?> handlers;
    private final Settings setting;
    private final ICrawlerFactory crawlerFactory;

    public AnnotationCrawlerManagerFactory(@NonNull SchedulerSetting schedulerSetting, @NonNull Set<? extends URL> startUrls,
                                           @NonNull Set<?> handlers, @NotNull ICrawlerFactory factory) {
        val pageSettings = handlers.stream()
                .map(h -> Preconditions.checkNotNull(h.getClass().getAnnotation(Page.class), "Missing %s annotation for %s", Page.class, h))
                .map(AnnotationCrawlerManagerFactory::toPageSettings)
                .collect(Collectors.toList());

        this.setting = Settings.builder()
                .schedulerSetting(schedulerSetting)
                .startUrls(startUrls)
                .pageSettings(pageSettings)
                .build();

        this.handlers = handlers;
        this.crawlerFactory = factory;
    }

    @Override
    public ICrawlerManager create() {
        return crawlerFactory.create(setting);
    }

    private static PageSetting toPageSettings(Page page) {
        return PageSetting.builder()
                .id(new PageID(page.id()))
                .minWeight(AnalyzeWeight.ofValue(page.minWeight()))
                .analyzeTemplates(toAnalyzeTemplates(page))
                .selectSettings(toUrlAnalyzeTemplates(page))
                .build();
    }

    private static Collection<? extends AnalyzeTemplate> toAnalyzeTemplates(Page page) {
        return Arrays.stream(page.analyzers()).map(a -> new AnalyzeTemplate(a.selector(), a.weight()))
                .collect(Collectors.toList());
    }

    private static Collection<? extends UrlSelectSetting> toUrlAnalyzeTemplates(Page page) {
        return Arrays.stream(page.urlSelectors())
                .map(AnnotationCrawlerManagerFactory::toUrlAnalyzeTemplates)
                .collect(Collectors.toList());
    }

    @SneakyThrows(MalformedURLException.class)
    private static UrlSelectSetting toUrlAnalyzeTemplates(UrlAnalyzer urlAnalyzer) {
        if (TextUtils.isEmpty(urlAnalyzer.baseUrl())) {
            return new UrlSelectSetting(urlAnalyzer.selector(), urlAnalyzer.attribute());
        }

        return new UrlSelectSetting(urlAnalyzer.selector(), urlAnalyzer.attribute(), new URL(urlAnalyzer.baseUrl()));
    }

}
