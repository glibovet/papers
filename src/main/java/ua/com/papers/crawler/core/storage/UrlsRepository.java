package ua.com.papers.crawler.core.storage;

import lombok.NonNull;
import ua.com.papers.crawler.settings.JobId;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

public interface UrlsRepository {

    void storePending(@NonNull URL url, @NonNull JobId job);

    void storePending(@NonNull Collection<? extends URL> urls, @NonNull JobId job);

    void storeProcessing(@NonNull URL url, @NonNull JobId job);

    /**
     * @param job job identifier for which urls iterator should
     *            be returned
     * @return iterator instance to iterate over
     * pending url for processing
     */
    Iterator<URL> pendingUrlsIterator(JobId job);

}
