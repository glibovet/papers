package ua.com.papers.crawler.core.domain.schedule;

import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * <p>
 * This class contains methods to start/stop crawler and can be used to
 * manage crawler's job.
 * </p>
 * Created by Максим on 12/28/2016.
 */
public interface ICrawlerManager {

    interface ErrorCallback {

        void onException(@NotNull Throwable th);
    }

    /**
     * Starts crawling. Each invocation of this method will create a new crawler and start job in a separate
     * thread for if configuration allows this, in another case task will be inserted at the end of the job queue
     *
     * @param handlers      to process page parts, each handler should be annotated with
     *                      {@linkplain ua.com.papers.crawler.util.PageHandler} or
     *                      {@linkplain IllegalArgumentException} will be raised
     * @param crawlCallback callback to monitor progress status
     */
    void startCrawling(@NotNull Collection<Object> handlers, @NotNull ICrawler.Callback crawlCallback);

    /**
     * Starts indexing. Each invocation of this method will create a new page indexer and start job in a separate
     * thread for if configuration allows this, in another case task will be inserted at the end of the job queue
     *
     * @param indexCallback callback to monitor indexing status
     * @param handlers      to process page parts, each handler should be annotated with
     *                      {@linkplain ua.com.papers.crawler.util.PageHandler} or
     *                      {@linkplain IllegalArgumentException} will be raised
     */
    void startIndexing(@NotNull Collection<Object> handlers, @NotNull IPageIndexer.Callback indexCallback);

    boolean isCrawling();

    boolean isIndexing();

    /**
     * Releases resources hold by crawler and stops
     * it
     */
    void stop();

}
