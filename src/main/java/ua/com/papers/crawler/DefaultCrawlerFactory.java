package ua.com.papers.crawler;

import lombok.val;
import ua.com.papers.crawler.core.domain.Crawler;
import ua.com.papers.crawler.core.domain.PageIndexer;
import ua.com.papers.crawler.core.domain.analyze.AnalyzeManager;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.analyze.IPageAnalyzer;
import ua.com.papers.crawler.core.domain.analyze.PageAnalyzer;
import ua.com.papers.crawler.core.domain.format.FormatManagerFactory;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.schedule.CrawlerManager;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;
import ua.com.papers.crawler.core.domain.select.IUrlExtractor;
import ua.com.papers.crawler.core.domain.select.UrlExtractor;
import ua.com.papers.crawler.core.domain.storage.InMemoryRepo;
import ua.com.papers.crawler.settings.FormatTemplate;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.Settings;
import ua.com.papers.crawler.settings.UrlSelectSetting;
import ua.com.papers.crawler.util.ICrawlerFactory;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Максим on 11/27/2016.
 */
public final class DefaultCrawlerFactory implements ICrawlerFactory {

    /**
     * Default url selection criteria; all &lt;a&gt; tags with 'href' attribute will be extracted
     */
    private static final Collection<? extends UrlSelectSetting> DEF_SELECT_SETTINGS;

    private static DefaultCrawlerFactory instance;

    static {
        DEF_SELECT_SETTINGS = Collections.singletonList(new UrlSelectSetting("a[href]", "href"));
    }

    public static DefaultCrawlerFactory getInstance() {

        DefaultCrawlerFactory local = instance;

        if (local == null) {
            synchronized (DefaultCrawlerFactory.class) {
                instance = local = new DefaultCrawlerFactory();
            }
        }
        return local;
    }

    private DefaultCrawlerFactory() {
    }

    @Override
    public ICrawlerManager create(@NotNull Settings settings) {

        val analyzeManager = createAnalyzeManager(settings);
        val formatFactory = createFormatFactory(settings);
        val crawler = Crawler.builder()
                .analyzeManager(analyzeManager)
                .formatManagerFactory(formatFactory)
                .urlExtractor(createUrlExtractor(settings))
                .build();

        val scheduleSett = settings.getSchedulerSetting();

        CrawlerManager.Builder builder = CrawlerManager.builder()
                .crawler(crawler)
                .executorService(scheduleSett.getExecutorService())
                .startupDelay(scheduleSett.getStartupDelay())
                .indexDelay(scheduleSett.getIndexDelay())
                .startUrls(settings.getStartUrls())
                .repository(InMemoryRepo.getInstance());

        if (scheduleSett.isAllowIndex()) {
            builder.indexer(new PageIndexer(InMemoryRepo.getInstance(), formatFactory, analyzeManager));
        }

        return builder.build();
    }

    private static IFormatManagerFactory createFormatFactory(Settings settings) {
        return new FormatManagerFactory(
                settings.getPageSettings()
                        .stream()
                        .flatMap((Function<PageSetting, Stream<? extends FormatTemplate>>) setting -> setting.getFormatTemplates().stream())
                        .collect(Collectors.toList())
        );
    }

    private static IUrlExtractor createUrlExtractor(Settings settings) {
        return new UrlExtractor(settings.getPageSettings()
                .stream()
                .collect(Collectors
                        .toMap(PageSetting::getId,
                                s -> s.getSelectSettings().isEmpty() ? DEF_SELECT_SETTINGS : s.getSelectSettings())
                )
        );
    }

    private static IAnalyzeManager createAnalyzeManager(Settings settings) {
        return new AnalyzeManager(settings
                .getPageSettings()
                .stream()
                .collect(Collectors.toMap(s -> s, DefaultCrawlerFactory::createPageAnalyzer))
        );
    }

    private static IPageAnalyzer createPageAnalyzer(PageSetting setting) {
        return new PageAnalyzer(setting.getMinWeight(), setting.getId(), setting.getAnalyzeTemplates());
    }

}
