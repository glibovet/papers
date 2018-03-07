package ua.com.papers.services.crawler.unit.nbuv;

import lombok.extern.java.Log;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.util.Handler;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;

import java.util.logging.Level;

/**
 * Created by Максим on 12/10/2017.
 */
@PageHandler(id = 1)
@Log
@Component
public final class LogPage {

    @PreHandle
    public void onPrepare(Page page) {
        log.log(Level.INFO, String.format("#onPrepare %s, %s", getClass(), page.getUrl()));
    }

    @PostHandle
    public void onPageParsed(Page page) {
        log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));
    }

    @Handler(id = 1)
    public void onHandleUri1(Element element) {
        log("onHandleUri1", element);
    }

    @Handler(id = 2)
    public void onHandleUri2(Element element) {
        log("onHandleUri2", element);
    }

    @Handler(id = 3)
    public void onHandleUri3(Element element) {
        log("onHandleUri3", element);
    }

    @Handler(id = 4)
    public void onHandleUri4(Element element) {
        log("onHandleUri4", element);
    }

    @Handler(id = 5)
    public void onHandleUri5(Element element) {
        log("onHandleUri5", element);
    }

    private static void log(String method, Element element) {
        log.log(Level.INFO, String.format("%s %s", method, element));
    }

}
