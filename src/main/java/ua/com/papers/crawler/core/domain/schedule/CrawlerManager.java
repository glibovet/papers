package ua.com.papers.crawler.core.domain.schedule;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * <p>
 * Default implementation of {@linkplain ICrawlerManager}
 * </p>
 * Created by Максим on 12/29/2016.
 */
@Value
@Log
@Getter(value = AccessLevel.NONE)
public class CrawlerManager implements ICrawlerManager {

    IPageIndexer indexer;
    ICrawler crawler;
    ScheduledExecutorService executorService;
    Collection<URL> startUrls;
    long startupDelay;
    long indexDelay;

    CrawlProxy crawlProxy;

    @Data
    private final class CrawlProxy implements ICrawler.Callback {
        @Setter(value = AccessLevel.NONE)
        private volatile boolean isCrawling;
        private ICrawler.Callback original;

        @Override
        public void onStart() {
            isCrawling = true;
            original.onStart();
        }

        @Override
        public void onUrlEntered(@NotNull URL url) {
            original.onUrlEntered(url);
        }

        @Override
        public void onPageAccepted(@NotNull Page page) {
            indexer.addToIndex(page);
            original.onPageAccepted(page);
        }

        @Override
        public void onPageRejected(@NotNull Page page) {
            original.onPageRejected(page);
        }

        @Override
        public void onStop() {
            original.onStop();
            isCrawling = false;
        }

        @Override
        public void onException(@NotNull URL url, @NotNull Throwable th) {
            original.onException(url, th);
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private CrawlerManager(@NotNull ICrawler crawler, @NotNull ScheduledExecutorService executorService, long startupDelay,
                           long indexDelay, @NotNull IPageIndexer indexer,
                           @NotNull Collection<URL> startUrls) {

        if (Preconditions.checkNotNull(startUrls, "startUrls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");

        this.crawler = Preconditions.checkNotNull(crawler);
        this.executorService = Preconditions.checkNotNull(executorService);
        this.startUrls = startUrls;
        this.indexer = Preconditions.checkNotNull(indexer);
        this.startupDelay = CrawlerManager.minExecutorDelay(startupDelay);
        this.indexDelay = CrawlerManager.minExecutorDelay(indexDelay);
        this.crawlProxy = new CrawlProxy();
    }

    @Override
    public void startCrawling(@NotNull Collection<Object> handlers, @NotNull ICrawler.Callback crawlCallback) {

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(crawlCallback, "crawl callback == null");

        if (!crawlProxy.isCrawling()) {
            // start crawler job
            crawlProxy.setOriginal(crawlCallback);
            executorService.schedule(() -> {
                try {
                    crawler.start(crawlProxy, handlers, startUrls);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Error occurred while running crawler", e);
                }
            }, startupDelay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void startIndexing(@NotNull Collection<Object> handlers, @NotNull IPageIndexer.Callback indexCallback) {

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(indexCallback, "index callback == null");
        // runs periodical indexing
        executorService.scheduleWithFixedDelay(() -> {
            try {
                indexer.index(indexCallback, handlers);
            } catch (final Exception e) {
                log.log(Level.SEVERE, "Failed to start indexer service", e);
            }
        }, startupDelay, indexDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        executorService.shutdown();
    }

    @Override
    public void stop(long timeout, @Nullable ErrorCallback callback) {

        executorService.shutdownNow();

        try {
            executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            log.log(Level.WARNING, "Failed to stop executor service correctly", e);

            if (callback != null) {
                callback.onException(e);
            }
        }
    }

    /**
     * min delay which can be accepted by thread executor
     *
     * @param value argument to test
     * @return value if value is greater than zero or zero in another case
     */
    private static long minExecutorDelay(long value) {
        return value < 0L ? 0L : value;
    }

}
