package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import lombok.extern.java.Log;
import lombok.val;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.select.IUrlExtractor;
import ua.com.papers.crawler.util.PageHandler;

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

    private final Queue<URL> urls;
    private final IAnalyzeManager analyzeManager;
    private final IUrlExtractor urlExtractor;

    public Crawler(@NotNull Collection<URL> startUrls, @NotNull IAnalyzeManager analyzeManager, IUrlExtractor urlExtractor) {
        Preconditions.checkNotNull(startUrls);
        this.analyzeManager = Preconditions.checkNotNull(analyzeManager);
        this.urlExtractor = Preconditions.checkNotNull(urlExtractor);
        this.urls = new LinkedList<>(startUrls);
    }

    @Override
    public void start(@Nullable ICallback callback, @NotNull Collection<Object> handlers) {
        checkHandlers(handlers);

        if (callback != null) {
            callback.onStart();
        }

        val MAX_CONTAINER_SIZE = 100;
        // todo redo
        val crawledPages = new HashMap<URL, Collection<Page>>(MAX_CONTAINER_SIZE);

        while (!urls.isEmpty()
                /*replace with spec condition*/ && urls.size() <= MAX_CONTAINER_SIZE) {
            val url = urls.poll();
            Collection<Page> crawledPagesColl = crawledPages.get(url);

            if (callback != null) {
                callback.onUrlEntered(url);
            }

            try {

                val page = Crawler.parsePage(url, 5000);

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
                            .forEach(result -> urlExtractor.extract(result.getPageID(), page)
                                    .stream()
                                    .filter(u -> !urls.contains(u) && !crawledPages.containsKey(u))
                                    .forEach(urls::add)
                            );

                    if(callback != null) {
                        callback.onPageAccepted(page);
                    }
                    //todo format page and so on
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
    public void start(@NotNull Collection<Object> handlers) {
        start(null, handlers);
    }

    @Override
    public void stop() {

    }

    @Override
    public void stop(long wait) {

    }

    private static Page parsePage(URL url, int timeout) throws IOException {
        return new Page(url, DateTime.now(), Jsoup.parse(url, timeout));
    }

    private static void checkHandlers(Collection<Object> handlers) {
        //todo empty check
        for (final Object handler : handlers)
            if (!handler.getClass().isAnnotationPresent(PageHandler.class))
                throw new IllegalArgumentException(
                        String.format("%s class must be annotated with %s", handler.getClass(), PageHandler.class));
    }

}
