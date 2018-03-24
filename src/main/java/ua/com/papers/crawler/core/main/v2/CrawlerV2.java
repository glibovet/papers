package ua.com.papers.crawler.core.main.v2;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.main.ICrawler;
import ua.com.papers.crawler.core.main.util.ErrorIgnoringDecorator;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.select.IUrlExtractor;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;

/**
 * <p>Default implementation of {@linkplain ICrawler}</p>
 * Created by Максим on 12/1/2016.
 */
@Log
@Getter(value = AccessLevel.NONE)
public final class CrawlerV2 implements ICrawler {

    private final IAnalyzeManager analyzeManager;
    private final IUrlExtractor urlExtractor;
    private final OutFormatter formatManager;
    private final Settings settings;
    private final UrlsRepository urlsRepository;

    @NonFinal
    @NonNull
    private ICrawler state;

    public CrawlerV2(@NotNull IAnalyzeManager analyzeManager, @NonNull Settings settings,
                     @NotNull IUrlExtractor urlExtractor, @NotNull OutFormatter formatManager,
                     @NonNull UrlsRepository urlsRepository) {

        this.analyzeManager = analyzeManager;
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
    public void start(Callback callback) {
        state.start(new ErrorIgnoringDecorator(callback));
    }

    @Override
    public void start(@NotNull Callback callback, @NotNull Collection<?> handlers,
                      @NotNull Collection<? extends URL> urlsColl) {
        state.start(callback, handlers, urlsColl);
    }

    @Override
    public void stop() {
        state.stop();
    }

    synchronized void toStartedState(Callback callback) {
        val properties = new StartedState.Properties(settings, urlsRepository, formatManager, analyzeManager, urlExtractor);

        this.state = new StartedState(properties, this, callback);
    }

    synchronized void toStoppedState() {
        this.state = new StoppedState(settings, this);
    }

}
