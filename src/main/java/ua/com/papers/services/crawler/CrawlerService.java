package ua.com.papers.services.crawler;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.factory.ICrawlerFactory;
import ua.com.papers.crawler.core.main.ICrawler;
import ua.com.papers.crawler.core.main.PageIndexer;
import ua.com.papers.crawler.core.main.bo.Page;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * <p>
 * Simple implementation of {@linkplain ICrawlerService}
 * </p>
 * Created by Максим on 6/8/2017.
 */
@Log
public final class CrawlerService implements ICrawlerService, ICrawler.Callback, PageIndexer.Callback {

    private final Collection<? extends ICrawler> crawlers;
    private final SlackChannel crawlerChannel;
    private final SlackSession slackSession;

    public CrawlerService(Collection<? extends ICrawlerFactory> factories, SlackChannel crawlerChannel,
                          SlackSession slackSession, Handler handler) {
        this.crawlers = factories.stream().map(ICrawlerFactory::create).collect(Collectors.toList());
        this.crawlerChannel = crawlerChannel;
        this.slackSession = slackSession;

        log.addHandler(handler);
    }

    @Override
    public void startCrawling() {
        val sb = new StringBuilder("Starting crawler jobs: ");

        for (val crawler : crawlers) {
            sb.append(crawler.getSettings().getJob().getId()).append(',');
            crawler.start(this);
        }

        sb.setLength(sb.length() - 1);
        slackSession.sendMessage(crawlerChannel, sb.toString());
    }

    @Override
    public void stopCrawling() {
        val sb = new StringBuilder("Stopping crawler jobs: ");

        for (val crawler : crawlers) {
            sb.append(crawler.getSettings().getJob().getId()).append(',');
            crawler.stop();
        }

        sb.setLength(sb.length() - 1);
        slackSession.sendMessage(crawlerChannel, sb.toString());
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
