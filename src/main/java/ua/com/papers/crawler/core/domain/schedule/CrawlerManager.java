package ua.com.papers.crawler.core.domain.schedule;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.settings.SchedulerSetting;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Executors;
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
    Collection<URL> startUrls;
    SchedulerSetting setting;

    @NonFinal ScheduledExecutorService executorService;
    @NonFinal volatile boolean isCrawling;
    @NonFinal volatile boolean isIndexing;

    CrawlProxy crawlProxy;

    @Data
    private final class CrawlProxy implements ICrawler.Callback {

        private ICrawler.Callback original;

        @Override
        public void onStart() {
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
    private CrawlerManager(@NotNull ICrawler crawler, @NotNull SchedulerSetting setting, @NotNull IPageIndexer indexer,
                           @NotNull Collection<URL> startUrls) {

        if (Preconditions.checkNotNull(startUrls, "startUrls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");

        this.setting = Preconditions.checkNotNull(setting);
        this.crawler = Preconditions.checkNotNull(crawler);
        this.startUrls = startUrls;
        this.indexer = Preconditions.checkNotNull(indexer);
        this.crawlProxy = new CrawlProxy();
        this.executorService = createExecutor(setting.isSeparatedIndexing());
    }

    @Override
    public void startCrawling(@NotNull Collection<Object> handlers, @NotNull ICrawler.Callback crawlCallback) {

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(crawlCallback, "crawl callback == null");

        if (!isCrawling) {
            // start crawler job
            crawlProxy.setOriginal(crawlCallback);
            executorService.schedule(() -> {
                isCrawling = true;
                try {
                    crawler.start(crawlProxy, handlers, startUrls);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Error occurred while running crawler", e);
                } finally {
                    stop();
                    isCrawling = false;
                }
            }, setting.getStartupDelay(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void startIndexing(@NotNull Collection<Object> handlers, @NotNull IPageIndexer.Callback indexCallback) {

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(indexCallback, "index callback == null");

        if(!isIndexing) {
            // runs periodical indexing
            executorService.scheduleWithFixedDelay(() -> {
                isIndexing = true;
                try {
                    indexer.index(indexCallback, handlers);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Failed to start indexer service", e);
                } finally {
                    stop();
                    isIndexing = false;
                }
            }, setting.getStartupDelay(), setting.getIndexDelay(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean isCrawling() {
        return isCrawling;
    }

    @Override
    public boolean isIndexing() {
        return isIndexing;
    }

    @Override
    public void stop() {
        executorService.shutdown();
        executorService = createExecutor(setting.isSeparatedIndexing());
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
        } finally {
            executorService = createExecutor(setting.isSeparatedIndexing());
        }
    }

    private static ScheduledExecutorService createExecutor(boolean isSeparatedIndex) {

        if (!isSeparatedIndex) {
            return Executors.newSingleThreadScheduledExecutor();
        }
        return Executors.newScheduledThreadPool(2);
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
