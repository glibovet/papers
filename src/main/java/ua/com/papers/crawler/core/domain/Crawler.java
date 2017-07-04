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
import ua.com.papers.crawler.exception.PageProcessException;
import ua.com.papers.crawler.settings.Conditions;
import ua.com.papers.crawler.settings.SchedulerSetting;
import ua.com.papers.crawler.util.PageUtils;
import ua.com.papers.crawler.util.Preconditions;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
        callback.onStart();
        // processing urls queue
        val urls = new LinkedList<URL>(urlsColl);
        // accepted pages count
        val acceptedCnt = new AtomicInteger(0);
        val formatManager = formatManagerFactory.create(handlers);
        // hash set would take much time to recalculate hashes and copy data when growing;
        val crawledUrls = new TreeSet<URL>((o1, o2) -> o1.toExternalForm().compareTo(o2.toExternalForm()));
        val lock = new ReentrantReadWriteLock();
        val localExecutor = Crawler.createThreadFactory(schedulerSetting.getProcessingThreads(), callback);
        this.executor = localExecutor;
        // runs crawling in a loop
        @Value class Looper implements Runnable {

            int thIndex;

            @Override
            public void run() {

                try {
                    try {
                        await();
                    } catch (final InterruptedException e) {
                        log.log(Level.INFO, String.format("#Interrupted thread %s", Thread.currentThread()), e);
                        return;
                    }
                    URL url;
                    while ((url = pollUrl()) != null) {

                        try {
                            loop(url);
                        } catch (final PageProcessException e) {
                            log.log(Level.WARNING, String.format("Failed to extract page content for url %s", e.getUrl()), e);
                            callback.onCrawlException(e.getUrl(), e);
                        } catch (final InterruptedException e) {
                            log.log(Level.INFO, String.format("Interrupted thread %s", Thread.currentThread()), e);
                            break;
                        }
                    }
                } finally {
                    log.log(Level.INFO, String.format("#Thread %s finished job", Thread.currentThread()));
                    boolean stop;
                    val activeThreads = localExecutor.getActiveCount();
                    synchronized (urls) {
                        // should stop looping because we don't have
                        // urls to process, crawler always has at least
                        // one thread to perform a job that's why thread
                        // index should be 0
                        assert urls.isEmpty() || activeThreads == 1;
                        stop = urls.isEmpty() && (thIndex == 0 || activeThreads == 1);
                    }
                    if (stop) {
                        Crawler.shutdown(localExecutor);
                        callback.onStop();
                    }
                }
            }

            // checks whether thread
            // can continue url processing
            private URL pollUrl() {
                try {
                    lock.writeLock().lock();
                    val canRun = !Thread.currentThread().isInterrupted() && !urls.isEmpty()
                            && predicate.canRun(crawledUrls, acceptedCnt.get());
                    return canRun ? urls.poll() : null;
                } finally {
                    lock.writeLock().unlock();
                }
            }
            // makes thread await for execution
            private void await() throws InterruptedException {

                while (!Thread.currentThread().isInterrupted()) {

                    try {
                        lock.readLock().lock();
                        if (thIndex <= urls.size() - 1) {
                            // current thread can start url processing
                            // or should shutdown because processing queue
                            // is empty
                            break;
                        } else {
                            log.log(Level.INFO, String.format("Thread %s is waiting", Thread.currentThread()));
                        }
                    } finally {
                        lock.readLock().unlock();
                    }
                    Thread.sleep(schedulerSetting.getProcessingDelay());
                }
            }
            // runs url processing loop, the main logic resides here
            private void loop(final URL url) throws InterruptedException, PageProcessException {
                log.log(Level.INFO, String.format("Looping thread %s", Thread.currentThread()));

                callback.onUrlEntered(url);
                final Page page;

                try {
                    page = PageUtils.parsePage(url, Crawler.parsePageTimeout);
                } catch (final IOException e) {
                    throw new PageProcessException(e, url);
                }

                val analyzeRes = analyzeManager.analyze(page);
                crawledUrls.add(url);

                if (analyzeRes.isEmpty()) {
                    // analyzed page 'weight' doesn't satisfies any specified one in
                    // the analyze settings; NOTE that only pages with text content types
                    // can be analyzed by crawler
                    log.log(Level.INFO, String.format("Rejected page: url %s", url));
                    callback.onPageRejected(page);
                } else {
                    log.log(Level.INFO, String.format("Accepted page: url %s", url));
                    acceptedCnt.incrementAndGet();
                    analyzeRes.forEach(result -> {
                                // add urls to processing queue
                                val extracted = urlExtractor.extract(result.getPageID(), page);
                                try {
                                    lock.writeLock().lock();
                                    extracted.stream()
                                            .filter(u -> /*log N < N*/ !crawledUrls.contains(u) && !urls.contains(u))
                                            .forEach(urls::add);
                                } finally {
                                    lock.writeLock().unlock();
                                }

                                formatManager.processPage(result.getPageID(), page);
                            }
                    );
                    callback.onPageAccepted(page);
                }

                Thread.sleep(schedulerSetting.getProcessingDelay());
            }
        }

        for (int i = 0; i < schedulerSetting.getProcessingThreads(); ++i) {
            executor.execute(new Looper(i));
        }
        // await termination
        executor.shutdown();
    }

    @Override
    public void stop() {

        if(executor != null) {
            Crawler.shutdown(executor);
        }
    }

    private static void shutdown(ExecutorService executor) {
        try {
            executor.shutdownNow();
            executor.awaitTermination(0, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            log.log(Level.WARNING, "Stopped unexpectedly", e);
        }
    }

    private static ThreadPoolExecutor createThreadFactory(int threads, Callback callback) {
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
