package ua.com.papers.crawler.core.main;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import org.joda.time.DateTimeZone;
import ua.com.papers.crawler.core.analyze.Analyzer;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.FormatterFactory;
import ua.com.papers.crawler.core.storage.IPageIndexRepository;
import ua.com.papers.crawler.settings.Conditions;
import ua.com.papers.crawler.settings.SchedulerSetting;
import ua.com.papers.crawler.util.PageUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * <p>
 * This is thread-safe implementation of {@linkplain PageIndexer}
 * </p>
 * Created by Максим on 12/29/2016.
 */
@Log
@Value
@Getter(value = AccessLevel.NONE)
public class PageIndexerImp implements PageIndexer {

    private static final int PAGE_PARSE_TIMEOUT = 5_000;

    private static final String GMT_FORMAT = "E, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final DateTimeZone TIME_ZONE = DateTimeZone.forOffsetHours(0);

    IPageIndexRepository repository;
    Analyzer analyzer;
    FormatterFactory formatterFactory;
    SchedulerSetting setting;
    @NonFinal
    ExecutorService executor;

    public PageIndexerImp(@NotNull IPageIndexRepository repository, @NotNull FormatterFactory formatterFactory,
                          @NotNull Analyzer analyzer, @NotNull SchedulerSetting setting) {
        this.repository = Conditions.isNotNull(repository);
        this.formatterFactory = Conditions.isNotNull(formatterFactory);
        this.analyzer = Conditions.isNotNull(analyzer);
        this.setting = Conditions.isNotNull(setting);
    }

    @Override
    public void addToIndex(@NotNull Page page) {

        final String contentHash;

        try {
            contentHash = PageIndexerImp.toHashString(page.toDocument().outerHtml());
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate page content hash", e);
        }

        repository.store(new IPageIndexRepository.Index(page.getVisitTime(), page.getUrl(), contentHash));
    }

    @Override
    public void index(@NotNull IndexingCallback callback, @NotNull Collection<Object> handlers) {
        PageIndexerImp.checkPreConditions(callback, handlers);
        stop();
        // iterator implementation, for example, can be
        // just an ArrayList iterator for a relatively small repositories
        // or it can be database cursor
        val iterator = repository.indexedPagesIterator();
        val formatManager = formatterFactory.create(handlers);
        val lock = new Object();
        callback.onStart();

        class Looper implements Runnable {

            @Override
            public void run() {

                IPageIndexRepository.Index index;

                while (!Thread.currentThread().isInterrupted() && (index = next()) != null) {
                    //log.log(Level.INFO, String.format("Looping thread %s", Thread.currentThread()));

                    try {

                        val page = PageUtils.parsePage(index.getUrl(), PAGE_PARSE_TIMEOUT);
                        val indexRes = analyzer.analyze(page);

                        if (indexRes.isEmpty()) {
                            // page doesn't conform analyze
                            // settings anymore
                            callback.onNotMatching(page);
                        } else if (PageIndexerImp.hasChanges(page, index)) {
                            callback.onMatching(page);
                            synchronized (lock) {
                                indexRes.forEach(result -> {
                                    /*try {
                                        formatManager.formatPage(result.getId(), page, );
                                    } catch (ProcessException e) {
                                        e.printStackTrace();
                                    }*/
                                });
                            }
                        } else {
                          //  callback.onIndexed(page);
                        }

                        Thread.sleep(setting.getIndexDelay());
                    } catch (final IOException e) {
                        log.log(Level.WARNING, String.format("Failed to index %s", index));
                    } catch (final InterruptedException e) {
                        //log.log(Level.INFO, String.format("#Interrupted thread %s", Thread.currentThread()), e);
                        break;
                    }
                }
                //log.log(Level.INFO, String.format("#Thread %s finished job", Thread.currentThread()));
            }

            private IPageIndexRepository.Index next() {
                synchronized (lock) {
                    return iterator.hasNext() ? iterator.next() : null;
                }
            }
        }
        executor = Executors.newFixedThreadPool(setting.getIndexThreads());

        for (int i = 0; i < setting.getIndexThreads(); ++i) {
            executor.execute(new Looper());
        }
        executor.shutdown();
        callback.onStop();
    }

    @Override
    public void stop() {

        if (executor != null) {
            try {
                executor.shutdownNow();
                executor.awaitTermination(0, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException e) {
                log.log(Level.WARNING, "Stopped unexpectedly", e);
            }
        }
    }

    /**
     * If content type isn't changed since last check, then in case page can be treated as
     * document content hashes will be checked for equality, in another case "if-changed" header will
     * be sent on the index url
     */
    private static boolean hasChanges(Page page, IPageIndexRepository.Index index) {

        if (PageUtils.canParse(page.getContentType())) {
            // page can be transformed into document => compare content hashes
            try {
                return !PageIndexerImp.toHashString(page.toDocument().outerHtml()).equals(index.getContentHash());
            } catch (final NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } else {

            try {
                // send http request with 'If-Modified-Since' header
                // if server is properly configured, then it'll return
                // 304 (NOT MODIFIED) header, in another case 'last-modified'
                // field will be checked
                @Cleanup("disconnect") val huc = (HttpURLConnection) index.getUrl().openConnection();

                val lastVisit = index.getLastVisit().withZone(TIME_ZONE);

                huc.setRequestMethod("GET");
                huc.addRequestProperty("If-Modified-Since", lastVisit.toString(GMT_FORMAT));
                huc.connect();

                val code = huc.getResponseCode();

                if (code == HttpURLConnection.HTTP_OK) {
                    // check last modified field if specified
                    return huc.getLastModified() == 0 // 'last-modified' field wasn't specified, reload
                            || huc.getLastModified() >= lastVisit.getMillis();
                }
                // no error and not modified since last visit
                return code < 400 && code != HttpURLConnection.HTTP_NOT_MODIFIED;
            } catch (final IOException e) {
                // not fatal network error occurred
                log.log(Level.WARNING, String.format("Failed to process index: %s", index.getUrl()));
            }
        }
        return true;
    }

    private static String toHashString(String str) throws NoSuchAlgorithmException {
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        messageDigest.reset();
        messageDigest.update(str.getBytes(Charset.forName("UTF8")));

        final byte[] resultByte = messageDigest.digest();
        final StringBuilder buffer = new StringBuilder();

        for (byte mByte : resultByte) {
            buffer.append(Integer.toHexString((mByte & 0xFF) | 0x100).substring(1, 3));
        }
        return buffer.toString();
    }

    private static void checkPreConditions(IndexingCallback callback, Collection<Object> handlers) {

        Preconditions.checkNotNull(callback, "callback == null");

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");
    }

}
