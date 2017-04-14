package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.*;
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
    static int parsePageTimeout = 5_000;
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
    public void start(@Nullable Callback callback, @NotNull Collection<Object> handlers,
                      @NotNull Collection<URL> urlsColl) {

        Crawler.checkPreConditions(handlers, urlsColl);
        stop();

        if (callback != null) {
            callback.onStart();
        }

        try {
            // runs all analyzing job
            runLoop(callback, handlers, urlsColl);
        } finally {
            // guaranties that callback's #stop
            // method will be called even if exception
            // occurs
            if (callback != null) {
                callback.onStop();
            }
        }
    }

    @Override
    public void stop() {

        if(executor != null) {

            log.log(Level.INFO, "#onStop");

            try {
                executor.shutdownNow();
                executor.awaitTermination(0, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException e) {
                log.log(Level.WARNING, "Stopped unexpectedly", e);
            }
        }
    }

    private void runLoop(Callback callback, Collection<Object> handlers, Collection<URL> urlsColl) {
        // processing urls queue
        val urls = new LinkedList<URL>(urlsColl);
        // accepted pages count
        val acceptedCnt = new AtomicInteger(0);
        val formatManager = formatManagerFactory.create(handlers);
        // hash set would take much time to recalculate hashes and copy data when growing;
        val crawledUrls = new TreeSet<URL>((o1, o2) -> o1.toExternalForm().compareTo(o2.toExternalForm()));
        val lock = new ReentrantReadWriteLock();
        // runs crawling in a loop
        @Value
        class Looper implements Runnable {

            int thIndex;

            @Override
            public void run() {

                try {
                    await();
                }  catch (final InterruptedException e) {
                    log.log(Level.INFO, String.format("Interrupted thread %s", Thread.currentThread()), e);
                    return;
                }

                while (canRun()) {

                    log.log(Level.INFO, Thread.currentThread()+"");

                    try {
                        loop();
                    } catch (final PageProcessException e) {
                        log.log(Level.WARNING, String.format("Failed to extract page content for url %s", e.getUrl()), e);

                        if (callback != null) {
                            callback.onException(e.getUrl(), e);
                        }
                    } catch (final InterruptedException e) {
                        log.log(Level.INFO, String.format("Interrupted thread %s", Thread.currentThread()), e);
                        break;
                    }
                }
                log.log(Level.INFO, "#Thread finished " + Thread.currentThread());
            }
            // checks whether thread
            // can continue url processing
            private boolean canRun() {
                try {
                    lock.readLock().lock();
                    return !Thread.currentThread().isInterrupted() && !urls.isEmpty()
                            && predicate.canRun(crawledUrls, acceptedCnt.get());
                } finally {
                    lock.readLock().unlock();
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
                            log.log(Level.INFO, "awaiting " + Thread.currentThread());
                        }
                    } finally {
                        lock.readLock().unlock();
                    }
                    Thread.sleep(schedulerSetting.getProcessingDelay());
                }
            }
            // runs url processing loop, the main logic resides here
            private void loop() throws InterruptedException, PageProcessException {

                final URL url;

                try {
                    lock.writeLock().lock();
                    url = urls.poll();
                } finally {
                    lock.writeLock().unlock();
                }

                if (callback != null) {
                    callback.onUrlEntered(url);
                }

                final Page page;

                try {
                    page = PageUtils.parsePage(url, parsePageTimeout);
                } catch (IOException e) {
                    throw new PageProcessException(e, url);
                }

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
                    acceptedCnt.incrementAndGet();
                    analyzeRes.forEach(result -> {
                                // add urls
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

                    if (callback != null) {
                        callback.onPageAccepted(page);
                    }
                }

                Thread.sleep(schedulerSetting.getProcessingDelay());
            }
        }

        if(executor != null) {
            stop();
        }

        //Conditions.isNull(executor, "Executor wasn't released properly");
        Collection<Future<?>> futures = new ArrayList<>(schedulerSetting.getProcessingThreads());
        executor = Executors.newFixedThreadPool(schedulerSetting.getProcessingThreads());

        for (int i = 0; i < schedulerSetting.getProcessingThreads(); ++i) {
            futures.add(executor.submit(new Looper(i)));
        }

        for(Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // await termination
        executor.shutdown();
    }

    /**
     * Checks method preconditions; if preconditions aren't satisfied, then instance of
     * {@linkplain IllegalArgumentException} will be thrown
     */
    private static void checkPreConditions(Collection<Object> handlers, Collection<URL> urlsColl) {

        if (Conditions.isNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");

        if (Conditions.isNotNull(urlsColl, "urls == null").isEmpty())
            throw new IllegalArgumentException("no start urls passed");
    }

}
