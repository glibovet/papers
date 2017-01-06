package ua.com.papers.crawler;

import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PartHandle;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;

/**
 * Created by Максим on 11/27/2016.
 */
@PageHandler(pageId = 1)
public class HandlerDemo {

    @PreHandle
    public void onPrepare() {
        // prepare instance
        System.out.println("onPrepare#");
    }

    @PostHandle
    public void onFinish(Page page) {
        // analyzing is done
        System.out.println("onFinish#");
    }

    @PartHandle(partId = 1)
    public void onHandlePart1(String str) {
        System.out.println("onHandlePart1#" + str);
    }

    @PartHandle(partId = 2)
    public void onHandlePart2(String str) {
        System.out.println("onHandlePart2#" + str);
    }

}
