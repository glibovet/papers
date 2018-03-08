package ua.com.papers.crawler.core.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.format.IFormatManager;
import ua.com.papers.crawler.core.select.IUrlExtractor;
import ua.com.papers.crawler.settings.SchedulerSetting;
import ua.com.papers.crawler.util.Preconditions;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Created by Максим on 12/3/2017.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log
final class LoopManager {

    LinkedList<URL> urls;
    Set<URL> crawledUrls;
    Set<Looper> pendingLoopers;
    AtomicInteger acceptedCnt, activeLoopers;
    @NonFinal @NotNull ThreadPoolExecutor executor;
    Random randomizer;

    Props props;

    @NonFinal
    boolean isRunning;

    @Builder
    @Value
    static class Props {
        ICrawlerPredicate predicate;
        ICrawler.Callback callback;
        Collection<Object> handlers;
        Collection<URL> urlsColl;
        IAnalyzeManager analyzeManager;
        IUrlExtractor urlExtractor;
        IFormatManager formatManager;
        SchedulerSetting schedulerSetting;
        int parseTimeout;
    }

    LoopManager(@NotNull Props props) {
        Preconditions.checkNotNull(props);

        this.props = props;
        this.urls = new LinkedList<>();
        this.acceptedCnt = new AtomicInteger(0);
        this.crawledUrls = new TreeSet<>((o1, o2) -> o1.toExternalForm().compareTo(o2.toExternalForm()));
        this.activeLoopers = new AtomicInteger(0);
        this.pendingLoopers = new HashSet<>(props.schedulerSetting.getIndexThreads());
        this.randomizer = new Random();
    }

    synchronized void start() {
        stop();

        props.callback.onStart();

        executor = LoopManager.createThreadFactory(props.schedulerSetting.getIndexThreads(), props.callback);

        val processingDelay = props.schedulerSetting.getIndexDelay();
        val threads = props.schedulerSetting.getIndexThreads();

        isRunning = true;
        pendingLoopers.clear();
        urls.addAll(props.urlsColl);
        activeLoopers.set(threads);

        for (var i = Looper.START_LOOPER_ID; i < threads; ++i) {
            val looper = new Looper(i, props.parseTimeout, processingDelay, this);

            pendingLoopers.add(looper);
            executor.execute(looper);
        }

        executor.shutdown();
    }

    synchronized void stop() {
        try {
            isRunning = false;
            // drop all pending urls
            while (urls.poll() != null) ;

            if (executor != null) {
                executor.shutdownNow();
                executor.awaitTermination(10, TimeUnit.SECONDS);
            }
        } catch (final InterruptedException e) {
            log.log(Level.WARNING, "Stopped unexpectedly", e);
        }
    }

    @NotNull
    synchronized Optional<URL> pollUrl() {
        var polled = Optional.<URL>empty();
        val pendingUrls = urls.size();
        val canRun = isRunning && props.predicate.canRun(crawledUrls, acceptedCnt.get())
                && pendingUrls > 0;

        if (canRun) {
            return Optional.of(urls.get(randomizer.nextInt(pendingUrls)));
        }

        return polled;
    }

    void notifyCrawlException(URL url, Throwable th) {
        props.callback.onCrawlException(url, th);
    }

    void notifyCrawlUncaughtException(Throwable th, Looper l) {
        log.log(Level.WARNING, String.format("An uncaught exception occurred in looper %s", l), th);
    }

    void notifyEnteredUrl(URL url) {
        props.callback.onUrlEntered(url);

        synchronized (this) {
            if (urls.size() >= props.schedulerSetting.getIndexThreads()) {
                for (val looper : pendingLoopers) {
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    synchronized (looper) {// oh common, Intellij, I know what I'm doing
                        looper.notifyAll();
                    }
                }
                pendingLoopers.clear();
            }
        }
    }

    void notifyPageEntered(Page page) {
        synchronized (this) {
            crawledUrls.add(page.getUrl());
        }

        props.callback.onUrlEntered(page.getUrl());

        val analyzeRes = props.analyzeManager.analyze(page);

        if (analyzeRes.isEmpty()) {
            // analyzed page 'weight' doesn't satisfies any specified one in
            // the analyze settings; NOTE that only pages with text content types
            // can be analyzed by crawler
            log.log(Level.INFO, String.format("Rejected page: url %s", page.getUrl()));
            props.callback.onPageRejected(page);
        } else {
            log.log(Level.INFO, String.format("Accepted page: url %s", page.getUrl()));

            acceptedCnt.incrementAndGet();
            props.callback.onPageAccepted(page);

            analyzeRes.forEach(result -> {
                        // add urls to processing queue
                        props.urlExtractor.extract(result.getPageID(), page)
                                .forEach(url -> {
                                    synchronized (LoopManager.this) {
                                        if ( /*log N < N*/ !crawledUrls.contains(url) && !urls.contains(url)) {
                                            urls.add(url);
                                        }
                                    }
                                });
                        props.formatManager.processPage(result.getPageID(), page);
                    }
            );
        }
    }

    synchronized void notifyLooperFinished() {
        val activeThreads = activeLoopers.decrementAndGet();

        if (urls.isEmpty() || activeThreads <= 0) {
            stop();
            props.callback.onStop();
        }
    }

    private static ThreadPoolExecutor createThreadFactory(int threads, ICrawler.Callback callback) {
        return new ThreadPoolExecutor(threads, threads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    val thread = new Thread(r);
                    thread.setDaemon(false);
                    thread.setUncaughtExceptionHandler((t, e) -> callback.onCrawlException(null, e));
                    return thread;
                });
    }

}
