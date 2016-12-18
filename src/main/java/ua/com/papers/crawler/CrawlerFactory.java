package ua.com.papers.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ua.com.papers.crawler.core.domain.Crawler;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.analyze.*;
import ua.com.papers.crawler.core.domain.select.IUrlExtractor;
import ua.com.papers.crawler.core.domain.select.UrlExtractor;
import ua.com.papers.crawler.settings.AnalyzeTemplate;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.Settings;
import ua.com.papers.crawler.settings.UrlSelectSetting;
import ua.com.papers.crawler.util.ICrawlerFactory;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Максим on 11/27/2016.
 */
@Service
public final class CrawlerFactory implements ICrawlerFactory {

    /**
     * Default url selection criteria; all &lt;a&gt; tags with 'href' attribute will be extracted
     */
    private static final Collection<? extends UrlSelectSetting> DEF_SELECT_SETTINGS;

    static {
        DEF_SELECT_SETTINGS = Collections.singletonList(new UrlSelectSetting("a[href]", "href"));
    }

    public CrawlerFactory() {
    }

    @Override
    public ICrawler create(@NotNull Settings settings) {
        return new Crawler(settings.getStartUrls(), createAnalyzeManager(settings), createUrlExtractor(settings));
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
                .collect(Collectors.toMap(s -> s, CrawlerFactory::createPageAnalyzer))
        );
    }

    private static IPageAnalyzer createPageAnalyzer(PageSetting setting) {
        return new PageAnalyzer(setting.getMinWeight(), setting.getId(), toChains(setting.getAnalyzeTemplates()));
    }

    private static Collection<? extends IAnalyzeChain> toChains(Collection<? extends AnalyzeTemplate> templates) {
        return templates
                .stream()
                .map(template ->
                        // page weight is result of multiplying number of found page parts using css selector
                        // by its (analyze chain) weight
                {
                    return new IAnalyzeChain() {
                        @Override
                        public int analyze(@NotNull(message = "cannot analyze null document") Document document) {
                            Elements elements = document.select(template.getCssSelector());

                            for (final Element element : elements) {
                                System.out.println(element);
                            }

                            return template.getWeight() * elements.size();
                        }
                    };
                })
                //(IAnalyzeChain) document -> document.select(template.getCssSelector()).size() * template.getWeight())
                .collect(Collectors.toList());
    }

}
