package ua.com.papers.crawler.core.domain.schedule;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.extern.java.Log;
import lombok.val;
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
public class Scheduler implements IScheduler {

    IPageIndexer indexer;
    ICrawler crawler;
    IPageIndexRepository repository;
    ScheduledExecutorService executorService;
    long startupDelay;
    long indexDelay;

    private class CallbackWrapper implements ICrawler.ICallback {

        private final ICrawler.ICallback original;

        private CallbackWrapper(ICrawler.ICallback original) {
            this.original = original;
        }

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
        }

        @Override
        public void onException(@NotNull Throwable th) {
            original.onException(th);
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private Scheduler(@NotNull ICrawler crawler, @NotNull ScheduledExecutorService executorService, long startupDelay,
                      long indexDelay, IPageIndexRepository repository, IPageIndexer indexer) {

        this.crawler = Preconditions.checkNotNull(crawler);
        this.executorService = Preconditions.checkNotNull(executorService);
        this.repository = Preconditions.checkNotNull(repository);
        this.indexer = indexer;
        this.startupDelay = Scheduler.minExecutorDelay(startupDelay);
        this.indexDelay = Scheduler.minExecutorDelay(indexDelay);
    }

    @Override
    public void start(@NotNull ICrawler.ICallback crawlCallback, @NotNull IPageIndexer.ICallback indexCallback,
                      @NotNull Collection<Object> handlers, @NotNull Collection<URL> startUrls) {
        doStart(crawlCallback, indexCallback, handlers, startUrls);
    }

    @Override
    public void start(@NotNull ICrawler.ICallback crawlCallback, @NotNull Collection<Object> handlers,
                      @NotNull Collection<URL> startUrls) {
        doStart(crawlCallback, null, handlers, startUrls);
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

    private void doStart(ICrawler.ICallback crawlCallback, IPageIndexer.ICallback indexCallback,
                         Collection<Object> handlers, Collection<URL> startUrls) {

        checkPreConditions(crawlCallback, indexCallback, handlers, startUrls);

        val mCrawlCall = prepareCrawlCallback(crawlCallback);
        // start crawler job
        executorService.schedule(() -> {
            try {
                crawler.start(mCrawlCall, handlers, startUrls);
            } catch (final Exception e) {
                log.log(Level.SEVERE, "Failed to start crawler", e);
            }
        }, startupDelay, TimeUnit.MILLISECONDS);

        if (indexer != null) {
            // periodical indexing if indexing wasn't disabled
            executorService.scheduleWithFixedDelay(() -> {
                try {
                    indexer.index(indexCallback, handlers);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Failed to start indexer service", e);
                }
            }, startupDelay, indexDelay, TimeUnit.MILLISECONDS);
        }
    }

    private ICrawler.ICallback prepareCrawlCallback(ICrawler.ICallback original) {
        return indexer == null ? original : new CallbackWrapper(original);
    }

    private void checkPreConditions(ICrawler.ICallback crawlCallback, IPageIndexer.ICallback indexCallback,
                                           Collection<Object> handlers, Collection<URL> startUrls) {
        Preconditions.checkNotNull(crawlCallback, "crawl callback == null");

        if(Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        if(Preconditions.checkNotNull(startUrls, "urls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");

        if(indexer != null && indexCallback == null)
            throw new NullPointerException(
                    "index callback wasn't passed. Either disable indexing or pass index callback");
    }

    private static long minExecutorDelay(long value) {
        return value < 0L ? 0L : value;
    }

}
