package ua.com.papers.crawler.core.main;

import ua.com.papers.crawler.core.main.model.Page;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * <p>
 * Base page analyzer contract
 * </p>
 * Created by Максим on 12/29/2016.
 */
public interface PageIndexer {

    /**
     * Adds page to indexer storage
     *
     * @param page page to index and store
     */
    void addToIndex(@NotNull Page page);

    /**
     * Runs index process
     *
     * @param callback callback to monitor process state
     * @param handlers handlers to process index results
     */
    void index(@NotNull IndexingCallback callback, @NotNull Collection<Object> handlers);

    /**
     * Stops indexer and releases
     * allocated resources
     */
    void stop();

}
