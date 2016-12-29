package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.val;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.IFormatManagerFactory;
import ua.com.papers.crawler.core.domain.storage.IPageIndexRepository;
import ua.com.papers.crawler.core.domain.storage.Index;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * This is thread-safe implementation of {@linkplain IPageIndexer}
 * </p>
 * Created by Максим on 12/29/2016.
 */
@Value
public class PageIndexer implements IPageIndexer {

    IPageIndexRepository repository;
    IAnalyzeManager analyzeManager;
    ReadWriteLock lock;
    IFormatManagerFactory formatManagerFactory;

    public PageIndexer(@NotNull IPageIndexRepository repository, IFormatManagerFactory formatManagerFactory, IAnalyzeManager analyzeManager) {
        this.repository = Preconditions.checkNotNull(repository);
        this.formatManagerFactory = Preconditions.checkNotNull(formatManagerFactory);
        this.analyzeManager = Preconditions.checkNotNull(analyzeManager);
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public void addToIndex(@NotNull Page page) {

        lock.writeLock().lock();

        try {
            repository.store(new Index(page.getVisitTime(), page.getUrl()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void index(@Nullable ICallback callback, @NotNull Collection<Object> handlers) {

        val cursor = repository.getIndexedPages();
        val formatManager = formatManagerFactory.create(handlers);

        callback.onStart();

        while (cursor.hasNext()) {
            val index = cursor.next();

            try {

                val page = parsePage(index.getUrl(), 5_000);
                val indexRes = analyzeManager.analyze(page);

                if (indexRes.isEmpty()) {
                    callback.onPageLost(page);
                } else {

                }

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        callback.onStop();
    }

    private static boolean checkModified(URL url, DateTime lastVisit) throws IOException {

        final HttpURLConnection huc = (HttpURLConnection) url.openConnection();

        try {
            huc.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        huc.addRequestProperty("If-Modified-Since", lastVisit
                .withZone(DateTimeZone.forOffsetHours(0)).toString("E, dd MMM yyyy hh:mm:ss 'GMT'"));
        huc.connect();

        return huc.getResponseCode() == 304;
    }

    private static Page parsePage(URL url, int timeout) throws IOException {
        return new Page(url, DateTime.now(), Jsoup.parse(url, timeout));
    }

}
