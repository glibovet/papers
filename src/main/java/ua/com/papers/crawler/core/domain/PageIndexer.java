package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.val;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.storage.IPageIndexRepository;
import ua.com.papers.crawler.core.domain.storage.Index;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * <p>
 * This is thread-safe implementation of {@linkplain IPageIndexer}
 * </p>
 * Created by Максим on 12/29/2016.
 */
@Value
//todo add errs handling
public class PageIndexer implements IPageIndexer {

    private static final int PAGE_PARSE_TIMEOUT = 5_000;

    IPageIndexRepository repository;
    IAnalyzeManager analyzeManager;
    IFormatManagerFactory formatManagerFactory;

    public PageIndexer(@NotNull IPageIndexRepository repository, IFormatManagerFactory formatManagerFactory, IAnalyzeManager analyzeManager) {
        this.repository = Preconditions.checkNotNull(repository);
        this.formatManagerFactory = Preconditions.checkNotNull(formatManagerFactory);
        this.analyzeManager = Preconditions.checkNotNull(analyzeManager);
    }

    @Override
    public void addToIndex(@NotNull Page page) {

        final String contentHash;

        try {
            contentHash = PageIndexer.toHashString(page.getDocument().outerHtml());
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate page content hash", e);
        }
        repository.store(new Index(page.getVisitTime(), page.getUrl(), contentHash));
    }

    @Override
    public void index(@NotNull ICallback callback, @NotNull Collection<Object> handlers) {
        PageIndexer.checkPreConditions(callback, handlers);
        // iterator implementation, for example, can be
        // just an ArrayList iterator for a relatively small repositories
        // or it can be database cursor
        val iterator = repository.getIndexedPages();
        val formatManager = formatManagerFactory.create(handlers);

        callback.onStart();

        while (iterator.hasNext()) {

            val index = iterator.next();

            try {

                val page = PageIndexer.parsePage(index.getUrl(), PAGE_PARSE_TIMEOUT);
                val indexRes = analyzeManager.analyze(page);

                if (indexRes.isEmpty()) {
                    // page doesn't conform analyze
                    // settings anymore
                    callback.onLost(page);
                } else if (PageIndexer.hasChanges(page, index)) {
                    callback.onUpdated(page);
                    // invoke page handlers
                    indexRes.forEach(result -> formatManager.processPage(result.getPageID(), page));
                } else {
                    callback.onIndexed(page);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        callback.onStop();
    }

    private static boolean hasChanges(Page page, Index index) {
        try {
            return !toHashString(page.getDocument().outerHtml()).equals(index.getContentHash());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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

    private static Page parsePage(URL url, int timeout) throws IOException {
        return new Page(url, DateTime.now(), Jsoup.parse(url, timeout));
    }

    private static void checkPreConditions(ICallback callback, Collection<Object> handlers) {

        Preconditions.checkNotNull(callback, "callback == null");

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("no handlers passed");
    }

}
