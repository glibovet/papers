package ua.com.papers.crawler.core.domain.schedule;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;

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

    CrawlProxy crawlProxy;
    IndexProxy indexProxy;

    private final class IndexProxy implements IPageIndexer.Callback {

        IPageIndexer.Callback original;
        volatile boolean isIndexing;

        @Override
        public void onStart() {
            original.onStart();
            isIndexing = true;
        }

        @Override
        public void onStop() {
            original.onStop();
            isIndexing = false;
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
        public void onException(@NotNull URL url, @NotNull Throwable th) {
            original.onException(url, th);
        }
    }

    private final class CrawlProxy implements ICrawler.Callback {

        ICrawler.Callback original;
        volatile boolean isCrawling;

        @Override
        public void onStart() {
            original.onStart();
            isCrawling = true;
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
    private CrawlerManager(@NotNull ICrawler crawler, @NotNull IPageIndexer indexer,
                           @NotNull Collection<URL> startUrls) {

        if (Preconditions.checkNotNull(startUrls, "startUrls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");

        this.crawler = Preconditions.checkNotNull(crawler);
        this.startUrls = startUrls;
        this.indexer = Preconditions.checkNotNull(indexer);
        this.crawlProxy = new CrawlProxy();
        this.indexProxy = new IndexProxy();
    }

    @Override
    public void startCrawling(@NotNull Collection<Object> handlers, @NotNull ICrawler.Callback crawlCallback) {

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(crawlCallback, "crawl callback == null");

        if (!crawlProxy.isCrawling) {
            // start crawler job
            crawlProxy.isCrawling = true;
            crawlProxy.original = crawlCallback;
            crawler.start(crawlProxy, handlers, startUrls);
        }
    }

    @Override
    public void startIndexing(@NotNull Collection<Object> handlers, @NotNull IPageIndexer.Callback indexCallback) {

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        Preconditions.checkNotNull(indexCallback, "index callback == null");

        if(!indexProxy.isIndexing) {
            indexProxy.isIndexing = true;
            indexProxy.original = indexCallback;
            indexer.index(indexProxy, handlers);
        }
    }

    @Override
    public boolean isCrawling() {
        return crawlProxy.isCrawling;
    }

    @Override
    public boolean isIndexing() {
        return indexProxy.isIndexing;
    }

    @Override
    public void stop() {
        crawler.stop();
        indexer.stop();
    }

}
