package ua.com.papers.crawler.core.storage;

import lombok.NonNull;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

public interface UrlsRepository {


    void storePending(@NonNull URL url);

    void storePending(@NonNull Collection<? extends URL> urls);

    void storeProcessing(@NonNull URL url);

    /**
     * @return iterator instance to iterate over
     * pending url for processing
     */
    Iterator<URL> pendingUrlsIterator();

}
