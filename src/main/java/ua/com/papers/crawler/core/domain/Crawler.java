package ua.com.papers.crawler.core.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.select.IUrlExtractor;
import ua.com.papers.crawler.settings.Conditions;
import ua.com.papers.crawler.settings.SchedulerSetting;
import ua.com.papers.crawler.util.Preconditions;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

/**
 * <p>Default implementation of {@linkplain ICrawler}</p>
 * Created by Максим on 12/1/2016.
 */
@Log
@Value
@Getter(value = AccessLevel.NONE)
public class Crawler implements ICrawler {

    /**
     * This predicate takes into count only available free memory
     */
    static ICrawlerPredicate DEFAULT_PREDICATE =
            (visitedUrls, acceptedPages) -> Runtime.getRuntime().freeMemory() > Crawler.getMinFreeMemory();

    @NonFinal
    static int parsePageTimeout = 5_000;// millis
    @NonFinal
    static long minFreeMemory = 20971520L;// 20 Mbytes

    IAnalyzeManager analyzeManager;
    IUrlExtractor urlExtractor;
    IFormatManagerFactory formatManagerFactory;
    ICrawlerPredicate predicate;
    SchedulerSetting schedulerSetting;

    @NonFinal LoopManager loopManager;

    @NonFinal ExecutorService executor;

    public static long getMinFreeMemory() {
        return minFreeMemory;
    }

    public static void setMinFreeMemory(long bytes) {
        Preconditions.checkArgument(bytes > 0, "bytes < 0");
        Crawler.minFreeMemory = bytes;
    }

    public static int getParsePageTimeout() {
        return parsePageTimeout;
    }

    public static void setParsePageTimeout(int parsePageTimeout) {
        Preconditions.checkArgument(parsePageTimeout > 0, "timeout < 0");
        Crawler.parsePageTimeout = parsePageTimeout;
    }

    @Value
    // because paranoia
    private static class IgnoreErrorCallback implements ICrawler.Callback {
        ICrawler.Callback delegate;

        @Override
        public void onStart() {
            invokeIgnoringError(delegate::onStart);
        }

        @Override
        public void onUrlEntered(@NotNull URL url) {
            invokeIgnoringError(() -> delegate.onUrlEntered(url));
        }

        @Override
        public void onPageAccepted(@NotNull Page page) {
            invokeIgnoringError(() -> delegate.onPageAccepted(page));
        }

        @Override
        public void onPageRejected(@NotNull Page page) {
            invokeIgnoringError(() -> delegate.onPageRejected(page));
        }

        @Override
        public void onStop() {
            invokeIgnoringError(delegate::onStop);
        }

        @Override
        public void onCrawlException(@Nullable URL url, @NotNull Throwable th) {
            invokeIgnoringError(() -> delegate.onCrawlException(url, th));
        }

        private static void invokeIgnoringError(Runnable action) {
            try {
                action.run();
            } catch (final Throwable th) {
                log.log(Level.WARNING, "Unexpected error occurred while invoking callback", th);
            }
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private Crawler(@NotNull IAnalyzeManager analyzeManager,
                    @NotNull IUrlExtractor urlExtractor, @NotNull IFormatManagerFactory formatManagerFactory,
                    @Nullable ICrawlerPredicate predicate, @NotNull SchedulerSetting schedulerSetting) {

        this.analyzeManager = Conditions.isNotNull(analyzeManager, "analyze manager == null");
        this.urlExtractor = Conditions.isNotNull(urlExtractor, "url extractor == null");
        this.formatManagerFactory = Conditions.isNotNull(formatManagerFactory, "format manager factory");
        this.predicate = predicate == null ? DEFAULT_PREDICATE : predicate;
        this.schedulerSetting = Conditions.isNotNull(schedulerSetting);
    }

    @Override
    public void start(@NotNull Callback callback, @NotNull Collection<Object> handlers,
                      @NotNull Collection<URL> urlsColl) {

        Crawler.checkPreConditions(callback, handlers, urlsColl);
        stop();

        val props = LoopManager.Props.builder()
                .callback(new IgnoreErrorCallback(callback))
                .handlers(handlers)
                .urlsColl(urlsColl)
                .analyzeManager(analyzeManager)
                .urlExtractor(urlExtractor)
                .formatManager(formatManagerFactory.create(handlers))
                .schedulerSetting(schedulerSetting)
                .parseTimeout(Crawler.parsePageTimeout)
                .predicate(predicate)
                .build();

        loopManager = new LoopManager(props);

        loopManager.start();
    }

    @Override
    public void stop() {

        if (loopManager != null) {
            loopManager.stop();
        }
    }

    /**
     * Checks method preconditions; if preconditions aren't satisfied, then instance of
     * {@linkplain IllegalArgumentException} will be thrown
     */
    private static void checkPreConditions(Callback callback, Collection<Object> handlers, Collection<URL> urlsColl) {

        Conditions.isNotNull(callback, "callback == null");

        if (Conditions.isNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        if (Conditions.isNotNull(urlsColl, "urls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");
    }

}
