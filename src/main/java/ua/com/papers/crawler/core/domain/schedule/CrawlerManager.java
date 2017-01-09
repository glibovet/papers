package ua.com.papers.crawler.core.domain.schedule;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.storage.IPageIndexRepository;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Максим on 12/29/2016.
 */
@Value
@Log
@Getter(value = AccessLevel.NONE)
public class CrawlerManager implements ICrawlerManager {

    IPageIndexer indexer;
    ICrawler crawler;
    IPageIndexRepository repository;
    ScheduledExecutorService executorService;
    Collection<URL> startUrls;
    long startupDelay;
    long indexDelay;

    CrawlProxy crawlProxy;
    IndexProxy indexProxy;

    @Data
    private static final class IndexProxy implements IPageIndexer.Callback {
        @Setter(value = AccessLevel.NONE)
        private volatile boolean isIndexing;
        private IPageIndexer.Callback original;

        @Override
        public void onStart() {
            isIndexing = true;
            original.onStart();
        }

        @Override
        public void onIndexed(@NotNull Page page) {
            original.onIndexed(page);
        }

        @Override
        public void onUpdated(@NotNull Page page) {
            original.onUpdated(page);
        }

        @Override
        public void onLost(@NotNull Page page) {
            original.onLost(page);
        }

        @Override
        public void onStop() {
            original.onStop();
            isIndexing = false;
        }
    }

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
            if (indexer != null) {
                indexer.addToIndex(page);
            }
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
        public void onException(@NotNull Throwable th) {
            original.onException(th);
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private CrawlerManager(@NotNull ICrawler crawler, @NotNull ScheduledExecutorService executorService, long startupDelay,
                           long indexDelay, @NotNull IPageIndexRepository repository, @NotNull IPageIndexer indexer,
                           @NotNull Collection<URL> startUrls) {

        if(Preconditions.checkNotNull(startUrls, "startUrls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");

        this.crawler = Preconditions.checkNotNull(crawler);
        this.executorService = Preconditions.checkNotNull(executorService);
        this.repository = Preconditions.checkNotNull(repository);
        this.startUrls = startUrls;
        this.indexer = indexer;
        this.startupDelay = CrawlerManager.minExecutorDelay(startupDelay);
        this.indexDelay = CrawlerManager.minExecutorDelay(indexDelay);
        this.crawlProxy = new CrawlProxy();
        this.indexProxy = new IndexProxy();
    }

    @Override
    public void startCrawling(@NotNull Collection<Object> handlers, @NotNull ICrawler.Callback crawlCallback) {

        if(Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(crawlCallback, "crawl callback == null");

        if(!crawlProxy.isCrawling()) {
            // start crawler job
            crawlProxy.setOriginal(crawlCallback);
            executorService.schedule(() -> {
                try {
                    crawler.start(crawlProxy, handlers, startUrls);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Failed to start crawler", e);
                }
            }, startupDelay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void startIndexing(@NotNull Collection<Object> handlers, @NotNull IPageIndexer.Callback indexCallback) {

        if(Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(indexCallback, "index callback == null");

        if(indexer == null)
            throw new IllegalStateException("indexing was disabled, enable indexing in the settings first!");

        if(!indexProxy.isIndexing()) {
            // periodical indexing if indexing wasn't disabled
            indexProxy.setOriginal(indexCallback);
            executorService.scheduleWithFixedDelay(() -> {
                try {
                    indexer.index(indexProxy, handlers);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Failed to start indexer service", e);
                }
            }, startupDelay, indexDelay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        executorService.shutdown();
    }

    @Override
    public void stop(long timeout) throws InterruptedException {
        executorService.shutdownNow();
        executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
    }

    private static long minExecutorDelay(long value) {
        return value < 0L ? 0L : value;
    }

}
