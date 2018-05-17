package ua.com.papers.crawler.core.main;

import lombok.NonNull;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.analyze.Analyzer;
import ua.com.papers.crawler.core.analyze.Result;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.main.model.PageID;
import ua.com.papers.crawler.core.main.model.PageStatus;
import ua.com.papers.crawler.core.main.util.CrawlErrorIgnoringDecorator;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.exception.ProcessException;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

@Log
final class CrawlingBehavior implements Behavior {

    private final CrawlingCallback callback;
    private final OutFormatter formatManager;
    private final Analyzer analyzer;
    private final UrlsRepository repository;
    private final Iterator<URL> urlsIterator;
    private final Settings settings;

    CrawlingBehavior(@NonNull CrawlingCallback callback, @NonNull OutFormatter formatManager,
                     @NonNull Analyzer analyzer, @NonNull UrlsRepository repository, @NonNull Settings settings) {
        this.callback = CrawlErrorIgnoringDecorator.wrap(callback);
        this.formatManager = formatManager;
        this.analyzer = analyzer;
        this.repository = repository;
        this.settings = settings;

        repository.store(settings.getStartUrls(), settings.getJob(), PageStatus.PENDING);

        this.urlsIterator = repository.urlsIterator(settings.getJob());
    }

    @Override
    public Iterator<URL> urlIterator() {
        return urlsIterator;
    }

    @Override
    public void onStart(Crawler crawler) {
        callback.onStart();
    }

    @Override
    public void onUrlEntered(URL url, Crawler crawler) {
        repository.store(url, settings.getJob(), PageStatus.PROCESSING);
        callback.onUrlEntered(url);
    }

    @Override
    @NotNull
    public Set<Result> extractMatchingPages(Page page, Crawler crawler) {
        return analyzer.matchingResults(page);
    }

    @Override
    public void onPageMatching(Page page, Crawler crawler, Set<Result> results) {
        callback.onPageAccepted(page);
        results.forEach(result -> processResult(page, result));
    }

    @Override
    public void onPageNotMatch(Page page, Crawler crawler, Set<Result> results) {
        callback.onPageRejected(page);
    }

    @Override
    public void onUrlParsed(URL url, Crawler crawler) {
        repository.add(url, settings.getJob(), PageStatus.PENDING);
    }

    @Override
    public void onStop(Crawler crawler) {
        callback.onStop();
    }

    @Override
    public void onCrawlException(URL url, Throwable th, Crawler crawler) {
        repository.store(url, settings.getJob(), PageStatus.FAILURE);
        callback.onCrawlException(url, th);
    }

    @Override
    public void onInternalException(Throwable th, Crawler crawler) {
        callback.onInternalException(th);
    }

    private void processResult(Page page, Result result) {
        try {
            formatManager.formatPage(result.getId(), page, getSettingsForPage(result.getId()));
        } catch (final ProcessException e) {
            log.log(Level.WARNING, String.format("format manager thrown an exception while handling page %s", page.getUrl()), e);
            repository.store(page.getUrl(), settings.getJob(), PageStatus.FAILURE);
        }
    }

    @NonNull
    private PageSetting getSettingsForPage(PageID id) {
        return settings.getPageSettings().stream().filter(s -> s.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Not found page settings for id %s", id)));
    }

}
