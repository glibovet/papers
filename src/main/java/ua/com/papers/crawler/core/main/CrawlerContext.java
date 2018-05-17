package ua.com.papers.crawler.core.main;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.analyze.Analyzer;
import ua.com.papers.crawler.core.main.util.CrawlErrorIgnoringDecorator;
import ua.com.papers.crawler.core.main.util.IndexErrorIgnoringDecorator;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.select.UrlExtractor;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;

/**
 * <p>Default implementation of {@linkplain Crawler}</p>
 * Created by Максим on 12/1/2016.
 */
@Log
@Getter(value = AccessLevel.NONE)
public final class CrawlerContext implements Crawler {

    private final Analyzer analyzer;
    private final UrlExtractor urlExtractor;
    private final OutFormatter formatManager;
    private final Settings settings;
    private final UrlsRepository urlsRepository;

    @NonFinal
    @NonNull
    private Crawler state;

    public CrawlerContext(@NotNull Analyzer analyzer, @NonNull Settings settings,
                          @NotNull UrlExtractor urlExtractor, @NotNull OutFormatter formatManager,
                          @NonNull UrlsRepository urlsRepository) {

        this.analyzer = analyzer;
        this.urlExtractor = urlExtractor;
        this.formatManager = formatManager;
        this.urlsRepository = urlsRepository;
        this.settings = settings;
        this.state = new StoppedState(settings, this);
    }

    @Override
    public Settings getSettings() {
        return state.getSettings();
    }

    @Override
    public void start(@NonNull CrawlingCallback callback) {
        state.start(CrawlErrorIgnoringDecorator.wrap(callback));
    }

    @Override
    public void start(@NonNull IndexingCallback callback) {
        state.start(IndexErrorIgnoringDecorator.wrap(callback));
    }

    @Override
    public void stop() {
        state.stop();
    }

    synchronized void toCrawlingState(CrawlingCallback crawlingCallback) {
        val behavior = new CrawlingBehavior(crawlingCallback, formatManager, analyzer, urlsRepository, settings);
        val schedulerSettings = settings.getSchedulerSetting();

        this.state = new StartedState(settings, urlExtractor, behavior, this,
                new StartedState.Config(schedulerSettings.getProcessingThreads(), schedulerSettings.getProcessingDelay()));
    }

    synchronized void toStoppedState() {
        this.state = new StoppedState(settings, this);
    }

    synchronized void toIndexingState(IndexingCallback indexingCallback) {
        val behavior = new IndexingBehavior(indexingCallback, formatManager, analyzer, urlsRepository, settings);
        val schedulerSettings = settings.getSchedulerSetting();

        this.state = new StartedState(settings, urlExtractor, behavior, this,
                new StartedState.Config(schedulerSettings.getIndexThreads(), schedulerSettings.getIndexDelay()));
    }

}
