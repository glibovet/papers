package ua.com.papers.crawler.core.main.v2;

import lombok.NonNull;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.main.ICrawler;
import ua.com.papers.crawler.settings.Settings;

import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;

@Log
public final class StoppedState implements ICrawler {

    private final Settings settings;
    private final CrawlerV2 context;

    StoppedState(@NonNull Settings settings, @NonNull CrawlerV2 context) {
        this.settings = settings;
        this.context = context;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void start(Callback callback) {
        context.toStartedState(callback);
    }

    @Override
    public void start(Callback callback, Collection<?> handlers, Collection<? extends URL> urls) {
        throw new IllegalStateException();
    }

    @Override
    public void stop() {
        log.log(Level.INFO, "Crawler is already stopped, skipping request");
    }
}
