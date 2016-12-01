package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import ua.com.papers.crawler.Page;
import ua.com.papers.crawler.util.PageHandler;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.*;

/**
 * Created by Максим on 12/1/2016.
 */
public final class DefaultCrawler implements ICrawler {

    private final Queue<URL> urls;
    private final Map<URL, Collection<Page>> crawledPages;
    private final int maxQueueSize;
    private ICallback callback;

    public DefaultCrawler(@NotNull Collection<URL> startUrls, int maxQueueSize) {
        Preconditions.checkNotNull(startUrls);
        this.maxQueueSize = maxQueueSize;
        this.urls = new LinkedList<>(startUrls);
        this.crawledPages = new HashMap<>(30);
    }

    @Override
    public void start(@NotNull ICallback callback, @NotNull Collection<Object> handlers) {
        checkHandlers(handlers);
        this.callback = callback;

    }

    @Override
    public void start(@NotNull Collection<Object> handlers) {
        start(null, handlers);
    }

    @Override
    public void stop() {

    }

    @Override
    public void stop(long wait) {

    }

    private static void checkHandlers(Collection<Object> handlers) {

        for (final Object handler : handlers)
            if (!handler.getClass().isAnnotationPresent(PageHandler.class))
                throw new IllegalArgumentException(
                        String.format("%s class must be annotated with %s", handler.getClass(), PageHandler.class));
    }

}
