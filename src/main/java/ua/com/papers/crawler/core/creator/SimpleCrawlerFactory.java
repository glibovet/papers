package ua.com.papers.crawler.core.creator;

import ua.com.papers.crawler.core.domain.ICrawlerPredicate;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.PageIndexer;
import ua.com.papers.crawler.core.domain.analyze.AnalyzeManager;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.analyze.IPageAnalyzer;
import ua.com.papers.crawler.core.domain.analyze.PageAnalyzer;
import ua.com.papers.crawler.core.domain.format.FormatManagerFactory;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.select.IUrlExtractor;
import ua.com.papers.crawler.core.domain.select.UrlExtractor;
import ua.com.papers.crawler.core.domain.storage.InMemoryRepo;
import ua.com.papers.crawler.settings.*;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * The simplest implementation of {@linkplain AbstractCrawlerFactory}.
 * </p>
 * <p>
 * You may override any method from inherited class to construct your
 * crawler from 'custom' parts
 * </p>
 * Created by Максим on 11/27/2016.
 */
public class SimpleCrawlerFactory extends AbstractCrawlerFactory {

    /**
     * Default url selection criteria; all &lt;a&gt; tags with 'href' attribute will be extracted
     */
    private static final Collection<? extends UrlSelectSetting> DEF_SELECT_SETTINGS;

    static {
        DEF_SELECT_SETTINGS = Collections.emptyList();
        //Collections.singletonList(new UrlSelectSetting("a[href]", "href"));
    }

    public SimpleCrawlerFactory() {
    }

    @Override
    @Nullable
    protected ICrawlerPredicate createRunPredicate() {
        return null;
    }

    @Override
    protected IFormatManagerFactory createFormatFactory(@NotNull Settings settings) {
        return new FormatManagerFactory(
                settings.getPageSettings()
                        .stream()
                        .flatMap(
                                (Function<PageSetting, Stream<? extends FormatTemplate>>) setting -> setting.getFormatTemplates().stream()
                        ).collect(Collectors.toList())
        );
    }

    @Override
    protected IUrlExtractor createUrlExtractor(@NotNull Settings settings) {
        return new UrlExtractor(settings.getPageSettings()
                .stream()
                .collect(Collectors
                        .toMap(PageSetting::getId,
                                s -> s.getSelectSettings().isEmpty() ? DEF_SELECT_SETTINGS : s.getSelectSettings())
                )
        );
    }

    @Override
    protected IAnalyzeManager createAnalyzeManager(@NotNull Settings settings) {
        return new AnalyzeManager(settings
                .getPageSettings()
                .stream()
                .collect(Collectors.toMap(s -> s, this::createPageAnalyzer))
        );
    }

    @Override
    protected IPageIndexer createPageIndexer(@NotNull Settings settings, @NotNull IFormatManagerFactory formatFactory,
                                             @NotNull IAnalyzeManager analyzeManager) {
        // creates page indexer that holds indexed pages in RAM!
        return new PageIndexer(InMemoryRepo.getInstance(), formatFactory, analyzeManager, settings.getSchedulerSetting());
    }

    private IPageAnalyzer createPageAnalyzer(PageSetting setting) {
        return new PageAnalyzer(setting.getMinWeight(), setting.getId(), setting.getAnalyzeTemplates());
    }

}
