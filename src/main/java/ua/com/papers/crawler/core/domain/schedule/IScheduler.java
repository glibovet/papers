package ua.com.papers.crawler.core.domain.schedule;

import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;

/**
 * Created by Максим on 12/28/2016.
 */
public interface IScheduler {

    /**
     * Makes crawler start doing job
     *
     * @param handlers      to process page parts, each handler should be annotated with
     *                      {@linkplain ua.com.papers.crawler.util.PageHandler} or
     *                      {@linkplain IllegalArgumentException} will be raised
     * @param crawlCallback callback to monitor progress status
     * @param indexCallback callback to monitor indexing status
     * @param startUrls     start urls to process
     */
    void start(@Nullable ICrawler.ICallback crawlCallback, @Nullable IPageIndexer.ICallback indexCallback,
               @NotNull Collection<Object> handlers, @NotNull Collection<URL> startUrls);

    /**
     * Releases resources hold by crawler and stops
     * it
     */
    void stop();

    /**
     * Releases resources hold by crawler and stops
     * it
     *
     * @param timeout millis to wait at most before finishing
     */
    void stop(long timeout) throws InterruptedException;

}
