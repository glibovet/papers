package ua.com.papers.services.crawler.unit.nbuv;

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
 * Created by Максим on 12/10/2017.
 */
@Log
@Component
@PageHandler(
        analyzers = @ContentAnalyzer(selector = "*"),
        baseUrl = "http://dspace.nbuv.gov.ua/",
        urlSelectors = {
                @UrlAnalyzer(selector = "#aspect_artifactbrowser_CommunityBrowser_div_comunity-browser > ul > li > ul > li.ds-artifact-item.community > div > div > a"),
                @UrlAnalyzer(selector = "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > .artifact-description > .artifact-title > a"),
                @UrlAnalyzer(selector = "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > div > div > a"),
                @UrlAnalyzer(selector = "#aspect_artifactbrowser_CollectionViewer_div_collection-view > div > ul > a"),
                @UrlAnalyzer(selector = "#aspect_artifactbrowser_ItemViewer_div_item-view > div.item-summary-view-metadata > p > a")
        }
)
public final class LogPage {

    @Handles(
            selectors = {
                    "#aspect_artifactbrowser_CommunityBrowser_div_comunity-browser > ul > li > ul > li.ds-artifact-item.community > div > div > a",
                    "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > .artifact-description > .artifact-title > a",
                    "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > div > div > a",
                    "#aspect_artifactbrowser_CollectionViewer_div_collection-view > div > ul > a",
                    "#aspect_artifactbrowser_ItemViewer_div_item-view > div.item-summary-view-metadata > p > a"
            }
    )
    public void onHandleUri(Element element, Page page) {
        log.log(Level.INFO, String.format("On handle uri %s of page %s", element.text(), page.getUrl()));
    }

}
