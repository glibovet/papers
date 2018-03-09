package ua.com.papers.services.crawler.unit.nbuv;

import lombok.extern.java.Log;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.process.OnHandle;
import ua.com.papers.crawler.core.processor.xml.annotation.Part;
import ua.com.papers.crawler.core.processor.xml.annotation.PageHandler;
import ua.com.papers.crawler.core.processor.xml.annotation.PostHandle;
import ua.com.papers.crawler.core.processor.xml.annotation.PreHandle;
import ua.com.papers.crawler.core.processor.annotation.analyze.Handler;
import ua.com.papers.crawler.core.processor.annotation.analyze.Analyze;
import ua.com.papers.services.crawler.UrlAdapter;

import java.util.logging.Level;

/**
 * Created by Максим on 12/10/2017.
 */
@PageHandler(id = 1)
@Log
@Component
@Handler(id = 1, rules = @Analyze(selector = "*"))
public final class LogPage {

    @PreHandle
    public void onPrepare(Page page) {
        log.log(Level.INFO, String.format("#onPrepare %s, %s", getClass(), page.getUrl()));
    }

    @PostHandle
    public void onPageParsed(Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));
    }

    @Part(id = 1)
    // preview
    @OnHandle(
            policy = OnHandle.CallPolicy.BEFORE,
            // todo guess adapter by an argument type
            converter = UrlAdapter.class,
            selectors = {
                    "#aspect_artifactbrowser_CommunityBrowser_div_comunity-browser > ul > li > ul > li.ds-artifact-item.community > div > div > a",
                    "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > .artifact-description > .artifact-title > a"
            }
    )
    public void onHandleUri1(Element element) {
        log("onHandleUri1", element);
    }

    @Part(id = 2)
    @OnHandle(selectors = "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > .artifact-description > .artifact-title > a")
    public void onHandleUri2(Element element) {
        log("onHandleUri2", element);
    }

    @Part(id = 3)
    @OnHandle(selectors = "#aspect_artifactbrowser_CommunityViewer_div_community-view > ul > li > div > div > a")
    public void onHandleUri3(Element element) {
        log("onHandleUri3", element);
    }

    @Part(id = 4)
    @OnHandle(selectors = "#aspect_artifactbrowser_CollectionViewer_div_collection-view > div > ul > a")
    public void onHandleUri4(Element element) {
        log("onHandleUri4", element);
    }

    @Part(id = 5)
    @OnHandle(selectors = "#aspect_artifactbrowser_ItemViewer_div_item-view > div.item-summary-view-metadata > p > a")
    public void onHandleUri5(Element element) {
        log("onHandleUri5", element);
    }

    private static void log(String method, Element element) {
        log.log(Level.INFO, String.format("%s %s", method, element));
    }

}
