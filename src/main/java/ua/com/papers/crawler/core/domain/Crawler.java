package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.select.IUrlExtractor;
import ua.com.papers.crawler.util.PageUtils;

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
@Value
public final class Crawler implements ICrawler {

    private static final int PARSE_PAGE_TIMEOUT = 5_000;
    @NonFinal
    private static long minFreeMemory = 20 * 1048576L;// 20 Mbytes

    IAnalyzeManager analyzeManager;
    IUrlExtractor urlExtractor;
    IFormatManagerFactory formatManagerFactory;
    ICrawlerPredicate predicate;

    @NonFinal
    @Getter(value = AccessLevel.NONE)
    private volatile boolean canRun;

    private static final ICrawlerPredicate DEFAULT_PREDICATE =
            (visitedUrls, acceptedPages) -> Runtime.getRuntime().freeMemory() > Crawler.getMinFreeMemory();

    public static long getMinFreeMemory() {
        return minFreeMemory;
    }

    public static void setMinFreeMemory(long bytes) {
        Preconditions.checkArgument(bytes > 0, "bytes < 0");
        Crawler.minFreeMemory = bytes;
    }

    @lombok.Builder(builderClassName = "Builder")
    private Crawler(@NotNull IAnalyzeManager analyzeManager,
                    @NotNull IUrlExtractor urlExtractor, @NotNull IFormatManagerFactory formatManagerFactory,
                    @NotNull ICrawlerPredicate predicate) {
        this.analyzeManager = Preconditions.checkNotNull(analyzeManager);
        this.urlExtractor = Preconditions.checkNotNull(urlExtractor);
        this.formatManagerFactory = Preconditions.checkNotNull(formatManagerFactory);
        this.predicate = predicate == null ? DEFAULT_PREDICATE : predicate;
    }

    @Override
    public void start(@Nullable Callback callback, @NotNull Collection<Object> handlers, @NotNull Collection<URL> urlsColl) {

        Crawler.checkPreConditions(handlers, urlsColl);

        if (callback != null) {
            callback.onStart();
        }

        final Queue<URL> urls = new LinkedList<>(urlsColl);
        int acceptedCnt = 0;
        val formatManager = formatManagerFactory.create(handlers);
        // hash set takes much time to recalculate and copy data when growing
        val crawledUrls = new TreeSet<URL>((o1, o2) -> o1.toExternalForm().compareTo(o2.toExternalForm()));
        canRun = true;

        while (canRun && !urls.isEmpty()
                && predicate.canRun(crawledUrls, acceptedCnt)) {

            val url = urls.poll();

            if (callback != null) {
                callback.onUrlEntered(url);
            }

            try {

                val page = PageUtils.parsePage(url, PARSE_PAGE_TIMEOUT);
                val analyzeRes = analyzeManager.analyze(page);

                crawledUrls.add(url);

                if (analyzeRes.isEmpty()) {
                    // analyzed page 'weight' doesn't satisfies any specified one in
                    // the analyze settings; NOTE that only pages with text content types
                    // can be analyzed by crawler
                    log.log(Level.INFO, String.format("Rejected page: url %s", url));

                    if (callback != null) {
                        callback.onPageRejected(page);
                    }
                } else {
                    log.log(Level.INFO, String.format("Accepted page: url %s", url));
                    acceptedCnt++;
                    analyzeRes
                            .forEach(result -> {
                                        // add urls
                                        urlExtractor.extract(result.getPageID(), page)
                                                .stream()
                                                .filter(u -> !urls.contains(u) && !crawledUrls.contains(u))
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

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        if (Preconditions.checkNotNull(urlsColl, "urls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");
    }

}
