package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import lombok.extern.java.Log;
import lombok.val;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.select.IUrlExtractor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Максим on 12/1/2016.
 */
@Log
public final class Crawler implements ICrawler {

    private static final int PARSE_PAGE_TIMEOUT = 5_000;

    private final IAnalyzeManager analyzeManager;
    private final IUrlExtractor urlExtractor;
    private final IFormatManagerFactory formatManagerFactory;

    private volatile boolean canRun;

    @lombok.Builder(builderClassName = "Builder")
    private Crawler(@NotNull IAnalyzeManager analyzeManager,
                   @NotNull IUrlExtractor urlExtractor, @NotNull IFormatManagerFactory formatManagerFactory) {
        this.analyzeManager = Preconditions.checkNotNull(analyzeManager);
        this.urlExtractor = Preconditions.checkNotNull(urlExtractor);
        this.formatManagerFactory = Preconditions.checkNotNull(formatManagerFactory);
    }

    @Override
    public void start(@Nullable ICallback callback, @NotNull Collection<Object> handlers, @NotNull Collection<URL> urlsColl) {

        Crawler.checkPreConditions(handlers, urlsColl);

        if (callback != null) {
            callback.onStart();
        }

        final Queue<URL> urls = new LinkedList<>(urlsColl);
        val formatManager = formatManagerFactory.create(handlers);
        val MAX_CONTAINER_SIZE = 100;
        // todo redo
        val crawledPages = new HashMap<URL, Collection<Page>>(MAX_CONTAINER_SIZE);
        canRun = true;

        while (canRun && !urls.isEmpty()
                /*replace with spec condition*/ && urls.size() <= MAX_CONTAINER_SIZE) {
            val url = urls.poll();
            Collection<Page> crawledPagesColl = crawledPages.get(url);

            if (callback != null) {
                callback.onUrlEntered(url);
            }

            try {

                val page = Crawler.parsePage(url, PARSE_PAGE_TIMEOUT);

                if (crawledPagesColl == null) {
                    crawledPagesColl = new ArrayList<>(1);
                }
                crawledPagesColl.add(page);
                crawledPages.put(url, crawledPagesColl);

                val analyzeRes = analyzeManager.analyze(page);

                if (analyzeRes.isEmpty()) {

                    log.log(Level.INFO, String.format("Rejected page: url %s", url));

                    if (callback != null) {
                        callback.onPageRejected(page);
                    }
                } else {
                    log.log(Level.INFO, String.format("Accepted page: url %s", url));
                    analyzeRes
                            .forEach(result -> {
                                        // add urls
                                        urlExtractor.extract(result.getPageID(), page)
                                                .stream()
                                                .filter(u -> !urls.contains(u) && !crawledPages.containsKey(u))
                                                .forEach(urls::add);
                                        // invoke page handlers
                                        formatManager.processPage(result.getPageID(), page);
                                    }
                            );

                    if (callback != null) {
                        callback.onPageAccepted(page);
                    }
                }
            } catch (final IOException e) {
                log.log(Level.WARNING, String.format("Failed to extract page content for url %s", url), e);

                if (callback != null) {
                    callback.onException(e);
                }
            }
        }

        if (callback != null) {
            callback.onStop();
        }
    }

    @Override
    public void stop() {
        canRun = false;
    }

    private static void checkPreConditions(Collection<Object> handlers, Collection<URL> urlsColl) {

        if(Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        if(Preconditions.checkNotNull(urlsColl, "urls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");
    }

    private static Page parsePage(URL url, int timeout) throws IOException {
        return new Page(url, DateTime.now(), Jsoup.parse(url, timeout));
    }

}
