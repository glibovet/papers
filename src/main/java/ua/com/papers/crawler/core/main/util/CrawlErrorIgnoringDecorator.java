package ua.com.papers.crawler.core.main.util;

import lombok.NonNull;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.main.CrawlingCallback;
import ua.com.papers.crawler.core.main.model.Page;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.logging.Level;

@Log
public final class CrawlErrorIgnoringDecorator implements CrawlingCallback {
    private final CrawlingCallback delegate;

    public static CrawlingCallback wrap(@NonNull CrawlingCallback delegate) {
        return new CrawlErrorIgnoringDecorator(delegate);
    }

    private CrawlErrorIgnoringDecorator(CrawlingCallback delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onStart() {
        invokeIgnoringError(delegate::onStart);
    }

    @Override
    public void onUrlEntered(@NotNull URL url) {
        invokeIgnoringError(() -> delegate.onUrlEntered(url));
    }

    @Override
    public void onPageAccepted(@NotNull Page page) {
        invokeIgnoringError(() -> delegate.onPageAccepted(page));
    }

    @Override
    public void onPageRejected(@NotNull Page page) {
        invokeIgnoringError(() -> delegate.onPageRejected(page));
    }

    @Override
    public void onStop() {
        invokeIgnoringError(delegate::onStop);
    }

    @Override
    public void onCrawlException(@Nullable URL url, @NotNull Throwable th) {
        invokeIgnoringError(() -> delegate.onCrawlException(url, th));
    }

    @Override
    public void onInternalException(Throwable th) {
        invokeIgnoringError(() -> delegate.onInternalException(th));
    }

    private static void invokeIgnoringError(Runnable action) {
        try {
            action.run();
        } catch (final Throwable th) {
            log.log(Level.WARNING, "Unexpected error occurred while invoking callback", th);
        }
    }
}
