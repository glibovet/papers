package ua.com.papers.crawler;

import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PartID;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;

/**
 * Created by Максим on 11/27/2016.
 */
@PageHandler(pageId = 1)
public class HandlerDemo {

    @PreHandle
    public void onPrepareContainer() {
        // prepare instance to fill
    }

    @PostHandle
    public void onFinish() {
        // analyzing is done
    }

    @PartID(partId = 1, escapeHtml = false)
    public void onHandleTitle(String title) {
        // append title to the article
    }

    @PartID(partId = 3)
    public void onHandleAnnotation(String annotation) {
        // append annotation to the article
    }

    @PartID(partId = 4)
    public void onHandleAuthor(String author) {
        // append author to the article
    }

}
