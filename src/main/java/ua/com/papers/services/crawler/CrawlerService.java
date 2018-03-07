package ua.com.papers.services.crawler;

import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;

/**
 * <p>
 * Simple implementation of {@linkplain ICrawlerService}
 * </p>
 * Created by Максим on 6/8/2017.
 */
@Service
@Log
public final class CrawlerService implements ICrawlerService, ICrawler.Callback, IPageIndexer.Callback {

    private final Collection<? extends WebSiteCrawlUnit> crawlUnits;

    @Autowired
    public CrawlerService(Collection<? extends WebSiteCrawlUnit> crawlUnits) {
        this.crawlUnits = crawlUnits;
    }

    @Override
    public void startCrawling() {
        for (val unit : crawlUnits) {
            unit.getManager().startCrawling(unit.getHandlers(), this);
        }
    }

    @Override
    public void stopCrawling() {
        for (val unit : crawlUnits) {
            unit.getManager().stopCrawling();
        }
    }

   /* @Override
    public boolean isCrawling() {
        return crawler.isCrawling();
    }

    @Override
    public void startReIndex() {
        crawler.startIndexing(composer.asHandlers(), this);
    }

    @Override
    public void stopReIndex() {
        crawler.stopIndexing();
    }

    @Override
    public boolean isReIndexing() {
        return crawler.isIndexing();
    }*/

    /* Crawler info callbacks */

    @Override
    public void onPageAccepted(@NotNull Page page) {
        log.info(String.format("Page with url %s was accepted", page.getUrl()));
    }

    @Override
    public void onStart() {
        log.info("Starting crawler");
    }

    @Override
    public void onUrlEntered(@NotNull URL url) {
        log.info(String.format("Discovering url %s", url));
    }

    @Override
    public void onPageRejected(@NotNull Page page) {
        log.info(String.format("Page with url %s was rejected", page.getUrl()));
    }

    @Override
    public void onStop() {
        log.info("Stopping crawler");
    }

    @Override
    public void onCrawlException(@Nullable URL url, @NotNull Throwable th) {
        log.log(Level.WARNING, String.format("Failed to process page with url %s", url), th);
    }

    /* Indexing info callbacks */

    @Override
    public void onIndexed(@NotNull Page page) {
        log.info(String.format("Page with url %s was re-indexed", page.getUrl()));
    }

    @Override
    public void onUpdated(@NotNull Page page) {
        log.info(String.format("Page with url %s was updated", page.getUrl()));
    }

    @Override
    public void onLost(@NotNull Page page) {
        log.info(String.format("Page with url %s doesn't satisfy requirements anymore", page.getUrl()));
    }

    @Override
    public void onIndexException(@NotNull URL url, @NotNull Throwable th) {
        log.log(Level.WARNING, String.format("Failed to re-index page with url %s", url), th);
    }


}
