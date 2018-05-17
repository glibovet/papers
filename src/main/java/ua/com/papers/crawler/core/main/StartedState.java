package ua.com.papers.crawler.core.main;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.analyze.Result;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.select.UrlExtractor;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
final class StartedState implements Crawler {

    private static final int PARSE_TIMEOUT = 15_000;// millis

    private final Settings settings;
    private final CrawlerContext context;
    private final Set<Looper> pendingLoopers;
    private final AtomicInteger parsedUrls, activeLoopers;
    private final ThreadPoolExecutor executor;
    private final Iterator<URL> urlsIterator;
    private final UrlExtractor urlExtractor;
    private final Behavior behavior;
    private final Config config;

    private final Object loopersLock = new Object();

    @AllArgsConstructor
    static final class Config {
        int threads;
        long delay;
    }

    StartedState(@NonNull Settings settings, @NonNull UrlExtractor urlExtractor,
                 @NonNull Behavior behavior, @NonNull CrawlerContext context, @NonNull Config config) {
        this.context = context;
        this.settings = settings;
        this.behavior = behavior;
        this.urlExtractor = urlExtractor;
        this.config = config;
        this.parsedUrls = new AtomicInteger(0);

        val indexThreads = settings.getSchedulerSetting().getIndexThreads();

        this.activeLoopers = new AtomicInteger(indexThreads);
        this.pendingLoopers = new HashSet<>(Math.max(indexThreads - 1, 1));
        this.urlsIterator = behavior.urlIterator();
        this.executor = createThreadFactory(indexThreads, behavior, this);

        crawl();
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void start(CrawlingCallback callback) {
        log.log(Level.INFO, "Crawler is already started, skipping request");
    }

    @Override
    public void start(IndexingCallback callback) {
        log.log(Level.INFO, "Shutting down, restarting instance in indexing mode");

        try {
            stop();
        } finally {
            context.toIndexingState(callback);
        }
    }

    @Override
    public synchronized void stop() {
        try {
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            log.log(Level.INFO, "Crawler stopped", e);
        } finally {
            context.toStoppedState();
            behavior.onStop(this);
        }
    }

    @NotNull
    synchronized Optional<URL> pollUrl() {
        return urlsIterator.hasNext() ? Optional.ofNullable(urlsIterator.next()) : Optional.empty();
    }

    void notifyCrawlException(URL url, Throwable th) {
        behavior.onCrawlException(url, th, this);
    }

    void notifyCrawlUncaughtException(Throwable th, Looper l) {
        log.log(Level.WARNING, String.format("An uncaught exception occurred in looper %s", l), th);
        behavior.onInternalException(th, this);
    }

    void notifyEnteredUrl(URL url) {
        behavior.onUrlEntered(url, this);
    }

    void notifyPageEntered(Page page) {
        behavior.onUrlEntered(page.getUrl(), this);

        val results = extractMatchingResults(page);

        if (results.isEmpty()) {
            log.log(Level.INFO, String.format("Rejected page: url %s", page.getUrl()));
            behavior.onPageNotMatch(page, this, results);

        } else {
            log.log(Level.INFO, String.format("Accepted page: url %s", page.getUrl()));
            behavior.onPageMatching(page, this, results);
            results.forEach(result -> processResult(page, result));
        }
    }

    private Set<Result> extractMatchingResults(Page page) {
        return behavior.extractMatchingPages(page, this).stream().filter(Result::isMatching)
                .collect(Collectors.toSet());
    }

    synchronized void notifyLooperFinished() {
        val activeThreads = activeLoopers.decrementAndGet();

        if (!urlsIterator.hasNext() || activeThreads <= 0) {
            stop();
        }
    }

    private void crawl() {
        behavior.onStart(this);
        executor.execute(new Looper(Looper.START_LOOPER_ID, StartedState.PARSE_TIMEOUT, config.delay, this));

        for (var i = Looper.START_LOOPER_ID + 1; i < config.threads; ++i) {
            pendingLoopers.add(new Looper(i, StartedState.PARSE_TIMEOUT, config.delay, this));
        }
    }

    private void processResult(Page page, Result result) {
        urlExtractor.extract(result.getId(), page).forEach(url -> {
            synchronized (loopersLock) {
                if (parsedUrls.incrementAndGet() >= pendingLoopers.size() && !pendingLoopers.isEmpty()) {
                    startPendingLoopers();
                }
            }
            behavior.onUrlParsed(url, this);
        });
    }

    private void startPendingLoopers() {
        for (val looper : pendingLoopers) {
            executor.execute(looper);
        }
        executor.shutdown();
        pendingLoopers.clear();
    }

    private static ThreadPoolExecutor createThreadFactory(int threads, Behavior crawlingCallback, StartedState state) {
        return new ThreadPoolExecutor(threads, threads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    val thread = new Thread(r);
                    thread.setDaemon(false);
                    thread.setUncaughtExceptionHandler((t, e) -> crawlingCallback.onInternalException(e, state));
                    return thread;
                });
    }

}
