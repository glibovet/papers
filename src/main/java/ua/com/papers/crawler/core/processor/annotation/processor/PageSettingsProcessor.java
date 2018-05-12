package ua.com.papers.crawler.core.processor.annotation.processor;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import ua.com.papers.crawler.core.main.vo.PageID;
import ua.com.papers.crawler.settings.AnalyzeTemplate;
import ua.com.papers.crawler.settings.AnalyzeWeight;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.UrlSelectSetting;
import ua.com.papers.crawler.settings.v2.PageHandler;
import ua.com.papers.crawler.settings.v2.analyze.UrlAnalyzer;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class PageSettingsProcessor {
    private final Collection<?> source;

    public PageSettingsProcessor(@NonNull Collection<?> source) {
        this.source = new ArrayList<>(source);
    }

    public List<PageSetting> process() {
        return source.stream()
                .map(h -> Preconditions.checkNotNull(h.getClass().getAnnotation(PageHandler.class), "Missing %s annotation for %s", PageHandler.class, h))
                .map(PageSettingsProcessor::toPageSettings)
                .collect(Collectors.toList());
    }

    private static PageSetting toPageSettings(PageHandler page) {
        return PageSetting.builder()
                .id(new PageID(page.id()))
                .minWeight(AnalyzeWeight.ofValue(page.minWeight()))
                .analyzeTemplates(toAnalyzeTemplates(page))
                .selectSettings(toUrlAnalyzeTemplates(page))
                .build();
    }

    private static Collection<? extends AnalyzeTemplate> toAnalyzeTemplates(PageHandler page) {
        return Arrays.stream(page.analyzers()).map(a -> new AnalyzeTemplate(a.selector(), a.weight()))
                .collect(Collectors.toList());
    }

    @SneakyThrows(MalformedURLException.class)
    private static Collection<? extends UrlSelectSetting> toUrlAnalyzeTemplates(PageHandler page) {
        val pageBaseUrl = TextUtils.isEmpty(page.baseUrl()) ? null : new URL(page.baseUrl());

        return Arrays.stream(page.urlSelectors())
                .map(analyzer -> PageSettingsProcessor.toUrlAnalyzeTemplates(analyzer, pageBaseUrl))
                .collect(Collectors.toList());
    }

    @SneakyThrows(MalformedURLException.class)
    private static UrlSelectSetting toUrlAnalyzeTemplates(UrlAnalyzer urlAnalyzer, @Nullable URL pageBaseUrl) {
        final URL url;

        if(TextUtils.isEmpty(urlAnalyzer.baseUrl())) {
            url = pageBaseUrl;
        } else {
            url = new URL(urlAnalyzer.baseUrl());
        }

        return new UrlSelectSetting(urlAnalyzer.selector(), urlAnalyzer.attribute(), url);
    }

}
