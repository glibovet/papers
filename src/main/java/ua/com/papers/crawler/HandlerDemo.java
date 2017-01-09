package ua.com.papers.crawler;

import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.convert.StringAdapter;
import ua.com.papers.crawler.util.*;

/**
 * Created by Максим on 11/27/2016.
 */
@PageHandler(id = 1)
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

    @Handler(id = 1, converter = StringAdapter.class)
    public void onHandlePart1(String str) {
        System.out.println("onHandlePart1#" + str);
    }

    @Handler(id = 2, converter = StringAdapter.class)
    public void onHandlePart2(String str) {
        System.out.println("onHandlePart2#" + str);
    }

    @Handler(id = 3, converter = ImageUrlAdapter.class)
    public void onHandleImage(Url url) {
        System.out.println("onHandleUrl# " + url);
    }

}
