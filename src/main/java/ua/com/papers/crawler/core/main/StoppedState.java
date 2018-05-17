package ua.com.papers.crawler.core.main;

import lombok.NonNull;
import lombok.extern.java.Log;
import ua.com.papers.crawler.settings.Settings;

import java.util.logging.Level;

@Log
final class StoppedState implements Crawler {

    private final Settings settings;
    private final CrawlerContext context;

    StoppedState(@NonNull Settings settings, @NonNull CrawlerContext context) {
        this.settings = settings;
        this.context = context;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void start(CrawlingCallback callback) {
        context.toCrawlingState(callback);
    }

    @Override
    public void start(IndexingCallback callback) {
        context.toIndexingState(callback);
    }

    @Override
    public void stop() {
        log.log(Level.INFO, "Crawler is already stopped, skipping request");
    }
}
