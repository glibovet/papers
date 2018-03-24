package ua.com.papers.crawler.core.main.v2;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.analyze.Result;
import ua.com.papers.crawler.core.main.ICrawler;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.exception.ProcessException;
import ua.com.papers.crawler.core.select.IUrlExtractor;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Log
public final class StartedState implements ICrawler {
    private static final int PARSE_TIMEOUT = 5_000;// millis

    private final Settings settings;
    private final CrawlerV2 context;

    private final Set<Looper> pendingLoopers;
    private final AtomicInteger parsedUrls, activeLoopers;

    private final ThreadPoolExecutor executor;
    private boolean isRunning = true;

    private final UrlsRepository repository;
    private final Iterator<URL> urlsIterator;
    private final OutFormatter formatManager;
    private final IAnalyzeManager analyzeManager;
    private final IUrlExtractor urlExtractor;
    private final Callback callback;

    private final Object loopersLock = new Object();

    @Value
    public static final class Properties {
        Settings settings;
        UrlsRepository repository;
        OutFormatter formatManager;
        IAnalyzeManager analyzeManager;
        IUrlExtractor urlExtractor;
    }

    StartedState(@NonNull Properties properties, @NonNull CrawlerV2 context, @NonNull Callback callback) {
        this.context = context;
        this.callback = callback;
        this.repository = properties.repository;
        this.settings = properties.settings;
        this.formatManager = properties.formatManager;
        this.analyzeManager = properties.analyzeManager;
        this.urlExtractor = properties.urlExtractor;
        this.parsedUrls = new AtomicInteger(0);

        val indexThreads = properties.settings.getSchedulerSetting().getIndexThreads();

        repository.storePending(settings.getStartUrls());

        this.activeLoopers = new AtomicInteger(indexThreads);
        this.pendingLoopers = new HashSet<>(Math.max(indexThreads - 1, 1));
        this.urlsIterator = properties.repository.pendingUrlsIterator();
        this.executor = createThreadFactory(indexThreads, callback);

        crawl();
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void start(Callback callback) {
        log.log(Level.INFO, "Crawler is already started, skipping request");
    }

    @Override
    public void start(Callback callback, Collection<?> handlers, Collection<? extends URL> urls) {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    public synchronized void stop() {
        try {
            isRunning = false;
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            log.log(Level.INFO, "Crawler stopped", e);
        } finally {
            context.toStoppedState();
            callback.onStop();
        }
    }

    @NotNull
    synchronized Optional<URL> pollUrl() {
        if (urlsIterator.hasNext()) {
            val polled = Optional.ofNullable(urlsIterator.next());

            if (isRunning) {
                return polled;
            }

            log.log(Level.INFO, "Stopping crawler because of external request");
        } else {
            log.log(Level.INFO, "Stopping crawler, no urls to poll");
        }

        return Optional.empty();
    }

    void notifyCrawlException(URL url, Throwable th) {
        callback.onCrawlException(url, th);
    }

    void notifyCrawlUncaughtException(Throwable th, Looper l) {
        log.log(Level.WARNING, String.format("An uncaught exception occurred in looper %s", l), th);
    }

    void notifyEnteredUrl(URL url) {
        callback.onUrlEntered(url);
    }

    void notifyPageEntered(Page page) {
        repository.storeProcessing(page.getUrl());
        callback.onUrlEntered(page.getUrl());

        val results = analyzeManager.analyze(page);

        if (results.isEmpty()) {
            // analyzed page 'weight' doesn't satisfies any specified one in
            // the analyze settings; NOTE that only pages with text content types
            // can be analyzed by the crawler
            log.log(Level.INFO, String.format("Rejected page: url %s", page.getUrl()));
            callback.onPageRejected(page);
        } else {
            log.log(Level.INFO, String.format("Accepted page: url %s", page.getUrl()));

            callback.onPageAccepted(page);
            results.stream().filter(Result::isMatching).forEach(result -> processResult(page, result));
        }
    }

    synchronized void notifyLooperFinished() {
        val activeThreads = activeLoopers.decrementAndGet();

        if (!urlsIterator.hasNext() || activeThreads <= 0) {
            stop();
        }
    }

    private void crawl() {
        callback.onStart();

        val processingDelay = settings.getSchedulerSetting().getIndexDelay();
        val threads = settings.getSchedulerSetting().getIndexThreads();

        executor.execute(new Looper(Looper.START_LOOPER_ID, StartedState.PARSE_TIMEOUT, processingDelay, this));

        for (var i = Looper.START_LOOPER_ID + 1; i < threads; ++i) {
            pendingLoopers.add(new Looper(i, StartedState.PARSE_TIMEOUT, processingDelay, this));
        }
    }

    private void processResult(Page page, Result result) {
        urlExtractor.extract(result.getId(), page).forEach(url -> {
            synchronized (loopersLock) {
                if (parsedUrls.incrementAndGet() >= pendingLoopers.size() && !pendingLoopers.isEmpty()) {
                    startPendingLoopers();
                }
            }
            repository.storePending(url);
        });

        try {
            formatManager.formatPage(result.getId(), page);
        } catch (final ProcessException e) {
            log.log(Level.WARNING, String.format("format manager thrown an exception while handling page %s", page.getUrl()), e);
        }
    }

    private void startPendingLoopers() {
        for (val looper : pendingLoopers) {
            executor.execute(looper);
        }
        executor.shutdown();
        pendingLoopers.clear();
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
