package ua.com.papers.services.crawler.unit.uran;

import lombok.extern.java.Log;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v2.PageHandler;
import ua.com.papers.crawler.settings.v2.analyze.ContentAnalyzer;
import ua.com.papers.crawler.settings.v2.analyze.UrlAnalyzer;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.util.logging.Level;

/**
 * Collects url to process
 */
@Log
@Component
@PageHandler(
        analyzers = {
                @ContentAnalyzer(selector = "#issues > a"),
                @ContentAnalyzer(selector = "#issues > h4 > a")
        },
        urlSelectors = {
                @UrlAnalyzer(selector = "#issue > h4 > a"),
                @UrlAnalyzer(selector = "#issues > a")
        }
)
public final class UranUrlCollector {

    @Handles(
            selectors = {
                    "#issues > a",
                    "#issue > h4 > a"
            }
    )
    public void onHandleUri(Element element, Page page) {
        log.log(Level.INFO, String.format("On handle uri %s of page %s", element.text(), page.getUrl()));
    }
}
