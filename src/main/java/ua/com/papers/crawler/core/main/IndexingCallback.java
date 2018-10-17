package ua.com.papers.crawler.core.main;

import ua.com.papers.crawler.core.main.model.Page;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * Callback to communicate with
 * {@linkplain PageIndexer} instance client
 */
public interface IndexingCallback {

    /**
     * Invoked before indexer begins job
     */
    default void onStart() {
    }

    /**
     * Invoked when page content was modified
     * since last index
     *
     * @param page page which was modified
     */
    void onMatching(@NotNull Page page);

    /**
     * Invoked when page doesn't conform analyzing
     * requirements anymore
     *
     * @param page lost page
     */
    void onNotMatching(@NotNull Page page);

    /**
     * Invoked when indexer meets exception
     *
     * @param url url which caused exception
     * @param th  occurred exception
     */
    void onIndexException(@NotNull URL url, @NotNull Throwable th);

    /**
     * Invoked when indexer meets exception
     *
     * @param th  occurred exception
     */
    void onInternalException(@NotNull Throwable th);

    /**
     * Invoked right before indexer finishes job
     */
    default void onStop() {
    }

}
