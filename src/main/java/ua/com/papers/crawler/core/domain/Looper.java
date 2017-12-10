package ua.com.papers.crawler.core.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.exception.PageProcessException;
import ua.com.papers.crawler.util.PageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Created by Максим on 12/3/2017.
 */
@Log
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode
@ToString
final class Looper implements Runnable {

    public static final int START_LOOPER_ID = 0;

    int thIndex, parseTimeout;
    long processingDelay;
    LoopManager loopManager;

    Looper(int thIndex, int parseTimeout, long processingDelay, LoopManager loopManager) {
        this.thIndex = thIndex;
        this.parseTimeout = parseTimeout;
        this.processingDelay = processingDelay;
        this.loopManager = loopManager;
    }

    @Override
    public void run() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> loopManager.notifyCrawlUncaughtException(e, Looper.this));

        try {
            try {
                awaitStart();
            } catch (final InterruptedException e) {
                log.log(Level.INFO, String.format("#Interrupted thread %s", Thread.currentThread()), e);
                return;
            }

            log.log(Level.INFO, String.format("#entering loop for thread %s", Thread.currentThread()));

            loop();

            log.log(Level.INFO, String.format("#Thread %s is successfully exiting the loop", Thread.currentThread()));

        } finally {
            log.log(Level.INFO, String.format("#Thread %s finished job", Thread.currentThread()));
            loopManager.notifyLooperFinished();
        }
    }

    private void awaitStart() throws InterruptedException {
        if (thIndex != Looper.START_LOOPER_ID) {
            synchronized (this) {
                wait();
            }
        }
    }

    // runs url processing loop
    private void loop() {
        Optional<URL> urlOptional;

        while ((urlOptional = loopManager.pollUrl()).isPresent()) {
            try {
                log.log(Level.INFO, String.format("Looping thread %s", Thread.currentThread()));

                assert urlOptional.isPresent();

                val url = urlOptional.get();

                loopManager.notifyEnteredUrl(url);
                final Page page;

                try {
                    page = PageUtils.parsePage(url, parseTimeout);
                } catch (final IOException e) {
                    throw new PageProcessException(e, url);
                }

                loopManager.notifyPageEntered(page);
                Thread.sleep(processingDelay);
            } catch (final PageProcessException e) {
                log.log(Level.WARNING, String.format("Failed to extract page content for url %s", e.getUrl()), e);
                loopManager.notifyCrawlException(e.getUrl(), e);
            } catch (final InterruptedException e) {
                log.log(Level.INFO, String.format("Interrupted thread %s", Thread.currentThread()), e);
                break;
            }
        }
    }

}
