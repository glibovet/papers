package ua.com.papers.services.crawler.unit.nbuv;

import lombok.extern.java.Log;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.settings.v1.PageHandler;
import ua.com.papers.crawler.settings.v1.Part;
import ua.com.papers.crawler.settings.v1.PostHandle;
import ua.com.papers.crawler.settings.v1.PreHandle;
import ua.com.papers.crawler.settings.v2.Page;
import ua.com.papers.crawler.settings.v2.analyze.ContentAnalyzer;
import ua.com.papers.crawler.settings.v2.analyze.UrlAnalyzer;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.util.logging.Level;

/**
 * Created by Максим on 12/10/2017.
 */
@PageHandler(id = 1)
@Log
@Component
@Page(id = 1,
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

    @PreHandle
    public void onPrepare(ua.com.papers.crawler.core.domain.bo.Page page) {
        log.log(Level.INFO, String.format("#onPrepare %s, %s", getClass(), page.getUrl()));
    }

    @PostHandle
    public void onPageParsed(ua.com.papers.crawler.core.domain.bo.Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));
    }

    @Handles(
            selectors = {
                    "#aspect_artifactbrowser_CommunityBrowser_div_comunity-browser > ul > li > ul > li.ds-artifact-item.community > div > div > a",
                    "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > .artifact-description > .artifact-title > a",
                    "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > div > div > a",
                    "#aspect_artifactbrowser_CollectionViewer_div_collection-view > div > ul > a",
                    "#aspect_artifactbrowser_ItemViewer_div_item-view > div.item-summary-view-metadata > p > a"
            }
    )
    public void onHandleUri(Element element, ua.com.papers.crawler.core.domain.bo.Page page /*inject page just for test*/) {
        log.log(Level.INFO, String.format("On handle uri %s of page %s", element.text(), page.getUrl()));
    }

    @Part(id = 1)
    public void onHandleUri1(Element element) {
        log("onHandleUri1", element);
    }

    @Part(id = 2)
    @Deprecated
    public void onHandleUri2(/*inject page just for test*/ ua.com.papers.crawler.core.domain.bo.Page page, Element element) {
        log("onHandleUri2", element);
    }

    @Part(id = 3)
    @Deprecated
    public void onHandleUri3(Element element) {
        log("onHandleUri3", element);
    }

    @Part(id = 4)
    @Deprecated
    public void onHandleUri4(Element element) {
        log("onHandleUri4", element);
    }

    @Part(id = 5)
    @Deprecated
    public void onHandleUri5(Element element) {
        log("onHandleUri5", element);
    }

    private static void log(String method, Element element) {
        log.log(Level.INFO, String.format("%s %s", method, element));
    }

}
