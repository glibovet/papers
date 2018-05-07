package ua.com.papers.crawler.core.main;

import ua.com.papers.crawler.core.main.bo.Page;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;

/**
 * <p>
 * Base page analyzer contract
 * </p>
 * Created by Максим on 12/29/2016.
 */
public interface PageIndexer {

    /**
     * Callback to communicate with
     * {@linkplain PageIndexer} instance client
     */
    interface Callback {

        /**
         * Invoked before indexer begins job
         */
        default void onStart() {
        }

        /**
         * Invoked when page neither updated nor lost
         *
         * @param page page which was indexed
         */
        void onIndexed(@NotNull Page page);

        /**
         * Invoked when page content was modified
         * since last index
         *
         * @param page page which was modified
         */
        void onUpdated(@NotNull Page page);

        /**
         * Invoked when page doesn't conform analyzing
         * requirements anymore
         *
         * @param page lost page
         */
        void onLost(@NotNull Page page);

        /**
         * Invoked when indexer meets exception
         *
         * @param url url which caused exception
         * @param th  occurred exception
         */
        void onIndexException(@NotNull URL url, @NotNull Throwable th);

        /**
         * Invoked right before indexer finishes job
         */
        default void onStop() {
        }

    }

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
    void index(@NotNull Callback callback, @NotNull Collection<Object> handlers);

    /**
     * Stops indexer and releases
     * allocated resources
     */
    void stop();

}
