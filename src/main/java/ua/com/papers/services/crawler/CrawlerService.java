package ua.com.papers.services.crawler;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.factory.CrawlerFactory;
import ua.com.papers.crawler.core.main.Crawler;
import ua.com.papers.crawler.core.main.CrawlingCallback;
import ua.com.papers.crawler.core.main.IndexingCallback;
import ua.com.papers.crawler.core.main.model.Page;

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
public final class CrawlerService implements ICrawlerService {

    private final Collection<? extends Crawler> crawlers;
    private final SlackChannel crawlerChannel;
    private final SlackSession slackSession;

    private final CrawlingCallback crawlingCallback;
    private final IndexingCallback indexingCallback;

    public CrawlerService(Collection<? extends CrawlerFactory> factories, SlackChannel crawlerChannel,
                          SlackSession slackSession, Handler handler) {
        this.crawlers = factories.stream().map(CrawlerFactory::create).collect(Collectors.toList());
        this.crawlerChannel = crawlerChannel;
        this.slackSession = slackSession;

        log.addHandler(handler);

        this.crawlingCallback = new CrawlingCallbackImp();
        this.indexingCallback = new IndexingCallbackImp();
    }

    @Override
    public void startCrawling() {
        val sb = new StringBuilder("Starting crawling jobs: ");

        for (val crawler : crawlers) {
            sb.append(crawler.getSettings().getJob().getId()).append(',');
            crawler.start(crawlingCallback);
        }

        sb.setLength(sb.length() - 1);
        slackSession.sendMessage(crawlerChannel, sb.toString());
    }

    @Override
    public void startIndexing() {
        val sb = new StringBuilder("Starting indexing jobs: ");

        for (val crawler : crawlers) {
            sb.append(crawler.getSettings().getJob().getId()).append(',');
            crawler.start(indexingCallback);
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


    private final class CrawlingCallbackImp implements CrawlingCallback {

        @Override
        public void onInternalException(Throwable th) {
            log.log(Level.SEVERE, "Internal crawler exception " + getCause(th), th);
        }

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
            log.log(Level.WARNING, String.format("Failed to process page with url %s, reason %s", url, getCause(th)), th);
        }

    }

    private final class IndexingCallbackImp implements IndexingCallback {

        @Override
        public void onStart() {
            log.info("Starting indexing");
        }

        @Override
        public void onStop() {
            log.info("Stopping crawler");
        }

        @Override
        public void onMatching(@NotNull Page page) {
            log.info(String.format("Page with url %s was updated", page.getUrl()));
        }

        @Override
        public void onNotMatching(@NotNull Page page) {
            log.info(String.format("Page with url %s doesn't satisfy requirements anymore", page.getUrl()));
        }

        @Override
        public void onIndexException(@NotNull URL url, @NotNull Throwable th) {
            log.log(Level.WARNING, String.format("Failed to re-index page with url %s, reason %s", url, getCause(th)), th);
        }

        @Override
        public void onInternalException(Throwable th) {
            log.log(Level.SEVERE, "Internal crawler exception " + getCause(th), th);
        }
    }

    private static Throwable getCause(Throwable e) {
        Throwable cause = null;
        Throwable result = e;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

}
