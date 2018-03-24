package ua.com.papers.crawler.core.main.util;

import lombok.NonNull;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.main.ICrawler;
import ua.com.papers.crawler.core.main.bo.Page;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.logging.Level;

@Log
public final class ErrorIgnoringDecorator implements ICrawler.Callback {
    private final ICrawler.Callback delegate;

    public ErrorIgnoringDecorator(@NonNull ICrawler.Callback delegate) {
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

    private static void invokeIgnoringError(Runnable action) {
        try {
            action.run();
        } catch (final Throwable th) {
            log.log(Level.WARNING, "Unexpected error occurred while invoking callback", th);
        }
    }
}
