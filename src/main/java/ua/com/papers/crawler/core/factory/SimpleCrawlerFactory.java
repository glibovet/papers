package ua.com.papers.crawler.core.factory;

import ua.com.papers.crawler.core.analyze.AnalyzeManager;
import ua.com.papers.crawler.core.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.analyze.IPageAnalyzer;
import ua.com.papers.crawler.core.analyze.PageAnalyzer;
import ua.com.papers.crawler.core.main.ICrawlerPredicate;
import ua.com.papers.crawler.core.processor.IFormatManagerFactory;
import ua.com.papers.crawler.core.processor.xml.XmlFormatManagerFactory;
import ua.com.papers.crawler.core.select.IUrlExtractor;
import ua.com.papers.crawler.core.select.UrlExtractor;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.Settings;
import ua.com.papers.crawler.settings.UrlSelectSetting;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

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
public abstract class SimpleCrawlerFactory extends AbstractCrawlerFactory {

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
        return new XmlFormatManagerFactory(settings);
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
        return new AnalyzeManager(settings.getPageSettings().stream().map(this::createPageAnalyzer).collect(Collectors.toList()));
    }

    private IPageAnalyzer createPageAnalyzer(PageSetting setting) {
        return new PageAnalyzer(setting.getMinWeight(), setting.getId(), setting.getAnalyzeTemplates());
    }

}
