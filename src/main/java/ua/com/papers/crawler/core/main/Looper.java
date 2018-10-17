package ua.com.papers.crawler.core.main;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.util.PageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Created by Максим on 12/3/2017.
 */
@Log
@EqualsAndHashCode
@ToString
final class Looper implements Runnable {

    public static final int START_LOOPER_ID = 0;

    private final int thIndex, parseTimeout;
    private final long processingDelay;
    private final StartedState crawler;

    Looper(int thIndex, int parseTimeout, long processingDelay, StartedState crawler) {
        this.thIndex = thIndex;
        this.parseTimeout = parseTimeout;
        this.processingDelay = processingDelay;
        this.crawler = crawler;
    }

    @Override
    public void run() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> crawler.notifyCrawlUncaughtException(e, Looper.this));

        try {
            //log.log(Level.INFO, String.format("#entering loop for thread %s", Thread.currentThread()));

            Optional<URL> urlOptional;
            // runs url processing loop
            while ((urlOptional = crawler.pollUrl()).isPresent()) {

                //log.log(Level.INFO, String.format("Looping thread %s", Thread.currentThread()));

                val url = urlOptional.get();

                try {
                    crawler.notifyEnteredUrl(url);

                    crawler.notifyPageEntered(PageUtils.parsePage(url, parseTimeout));

                    Thread.sleep(processingDelay);
                } catch (final IOException e) {
                    log.log(Level.WARNING, String.format("Failed to extract page content for url %s", url), e);
                    crawler.notifyCrawlException(url, e);
                } catch (final InterruptedException e) {
                    //log.log(Level.INFO, String.format("Interrupted thread %s", Thread.currentThread()), e);
                    break;
                }
            }

            //log.log(Level.INFO, String.format("#Thread %s is successfully exiting the loop", Thread.currentThread()));

        } finally {
            //log.log(Level.INFO, String.format("#Thread %s finished job", Thread.currentThread()));
            crawler.notifyLooperFinished();
        }
    }

}
