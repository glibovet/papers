package ua.com.papers.crawler.core.storage;

import lombok.NonNull;
import ua.com.papers.crawler.core.main.model.PageStatus;
import ua.com.papers.crawler.settings.JobId;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents repository that holds urls to be processed by crawler;
 * Implementations should care about data synchronization, locking and atomicity
 * since crawler don't know from where URL is being obtained from
 */
public interface UrlsRepository {

    /**
     * Adds url if such url doesn't exist in the collection
     */
    void add(@NonNull URL url, @NonNull JobId job, @NonNull PageStatus status);

    /**
     * Adds or updates given url
     */
    void store(@NonNull URL url, @NonNull JobId job, @NonNull PageStatus status);

    /**
     * Adds or updates all url in the collection
     */
    void store(@NonNull JobId job, @NonNull PageStatus status);

    /**
     * Adds or updates given urls in the collection
     */
    void store(@NonNull Collection<? extends URL> urls, @NonNull JobId job, PageStatus status);

    /**
     * @param job job identifier for which urls iterator should
     *            be returned
     * @return iterator instance to iterate over
     * pending url. Each subsequent call to {@linkplain Iterator#next()} should
     * lock url, so that it won't be processed by another crawler's internal
     * thread or remote running instance processing same job
     */
    Iterator<URL> urlsIterator(@NonNull JobId job);

}
