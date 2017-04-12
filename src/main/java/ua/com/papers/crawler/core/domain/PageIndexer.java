package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import org.joda.time.DateTimeZone;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.storage.IPageIndexRepository;
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
 * This is thread-safe implementation of {@linkplain IPageIndexer}
 * </p>
 * Created by Максим on 12/29/2016.
 */
@Log
@Value
@Getter(value = AccessLevel.NONE)
public class PageIndexer implements IPageIndexer {

    private static final int PAGE_PARSE_TIMEOUT = 5_000;

    private static final String GMT_FORMAT = "E, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final DateTimeZone TIME_ZONE = DateTimeZone.forOffsetHours(0);

    IPageIndexRepository repository;
    IAnalyzeManager analyzeManager;
    IFormatManagerFactory formatManagerFactory;
    SchedulerSetting setting;
    @NonFinal ExecutorService executor;

    public PageIndexer(@NotNull IPageIndexRepository repository, @NotNull IFormatManagerFactory formatManagerFactory,
                       @NotNull IAnalyzeManager analyzeManager, @NotNull SchedulerSetting setting) {
        this.repository = Conditions.isNotNull(repository);
        this.formatManagerFactory = Conditions.isNotNull(formatManagerFactory);
        this.analyzeManager = Conditions.isNotNull(analyzeManager);
        this.setting = Conditions.isNotNull(setting);
    }

    @Override
    public void addToIndex(@NotNull Page page) {

        final String contentHash;

        try {
            contentHash = PageIndexer.toHashString(page.toDocument().outerHtml());
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate page content hash", e);
        }

        repository.store(new IPageIndexRepository.Index(page.getVisitTime(), page.getUrl(), contentHash));
    }

    @Override
    public void index(@NotNull Callback callback, @NotNull Collection<Object> handlers) {
        PageIndexer.checkPreConditions(callback, handlers);
        stop();
        // iterator implementation, for example, can be
        // just an ArrayList iterator for a relatively small repositories
        // or it can be database cursor
        val iterator = repository.getIndexedPages();
        val formatManager = formatManagerFactory.create(handlers);
        val lock = new Object();
        callback.onStart();

        class Looper implements Runnable {

            @Override
            public void run() {

                IPageIndexRepository.Index index;

                while ((index = next()) != null) {

                    try {

                        val page = PageUtils.parsePage(index.getUrl(), PAGE_PARSE_TIMEOUT);
                        val indexRes = analyzeManager.analyze(page);

                        if (indexRes.isEmpty()) {
                            // page doesn't conform analyze
                            // settings anymore
                            callback.onLost(page);
                        } else if (PageIndexer.hasChanges(page, index)) {
                            callback.onUpdated(page);
                            synchronized (lock) {
                                indexRes.forEach(result -> formatManager.processPage(result.getPageID(), page));
                            }
                        } else {
                            callback.onIndexed(page);
                        }
                    } catch (final IOException e) {
                        log.log(Level.WARNING, String.format("Failed to index %s", index));
                    }
                }
            }

            private IPageIndexRepository.Index next() {
                synchronized (lock) {
                    return iterator.hasNext() ? iterator.next() : null;
                }
            }
        }
        Conditions.isNull(executor);
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
                return !PageIndexer.toHashString(page.toDocument().outerHtml()).equals(index.getContentHash());
            } catch (final NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } else {

            try {
                // send http request with 'If-Modified-Since' header
                // if server is properly configured, then it'll return
                // 304 (NOT MODIFIED) header, in another case 'last-modified'
                // field will be checked
                val huc = (HttpURLConnection) index.getUrl().openConnection();

                try {
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
                } finally {
                    huc.disconnect();
                }
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

    private static void checkPreConditions(Callback callback, Collection<Object> handlers) {

        Preconditions.checkNotNull(callback, "callback == null");

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");
    }

}
