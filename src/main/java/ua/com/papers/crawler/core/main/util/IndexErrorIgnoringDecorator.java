package ua.com.papers.crawler.core.main.util;

import lombok.NonNull;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.main.IndexingCallback;
import ua.com.papers.crawler.core.main.model.Page;

import java.net.URL;
import java.util.logging.Level;

@Log
public final class IndexErrorIgnoringDecorator implements IndexingCallback {

    private final IndexingCallback delegate;

    public static IndexErrorIgnoringDecorator wrap(@NonNull IndexingCallback delegate) {
        return new IndexErrorIgnoringDecorator(delegate);
    }

    private IndexErrorIgnoringDecorator(@NonNull IndexingCallback delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onStart() {
        invokeIgnoringError(delegate::onStart);
    }

    @Override
    public void onMatching(Page page) {
        invokeIgnoringError(() -> delegate.onMatching(page));
    }

    @Override
    public void onNotMatching(Page page) {
        invokeIgnoringError(() -> delegate.onNotMatching(page));
    }

    @Override
    public void onIndexException(URL url, Throwable th) {
        invokeIgnoringError(() -> delegate.onIndexException(url, th));
    }

    @Override
    public void onInternalException(Throwable th) {
        invokeIgnoringError(() -> delegate.onInternalException(th));
    }

    @Override
    public void onStop() {
        invokeIgnoringError(delegate::onStop);
    }

    private static void invokeIgnoringError(Runnable action) {
        try {
            action.run();
        } catch (final Throwable th) {
            log.log(Level.WARNING, "Unexpected error occurred while invoking callback", th);
        }
    }
}
