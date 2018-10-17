package ua.com.papers.crawler.core.main;

import ua.com.papers.crawler.core.main.model.Page;

import javax.validation.constraints.NotNull;
import java.net.URL;

public interface CrawlingCallback {
    /**
     * This callback gets invoked before
     * crawler starts do his job
     */
    default void onStart() {
    }

    /**
     * Called each time crawler starts to
     * process a new url
     *
     * @param url url which is being analyzed
     */
    default void onUrlEntered(@NotNull URL url) {
    }

    /**
     * Called each time crawler considers page
     * as acceptable
     *
     * @param page page which is considered to be acceptable
     */
    default void onPageAccepted(@NotNull Page page) {
    }

    /**
     * Called each time crawler considers page
     * as unacceptable
     *
     * @param page page which is considered to be unacceptable
     */
    default void onPageRejected(@NotNull Page page) {
    }

    /**
     * Called before crawler stops doing his job
     */
    default void onStop() {
    }

    /**
     * Called each time crawler fails to perform operation or
     * inner exception occurs. Can be used for logging
     *
     * @param url page's url which caused exception
     * @param th  failure cause
     */
    default void onCrawlException(@NotNull URL url, @NotNull Throwable th) {
    }

    /**
     * Called each time crawler fails to perform operation or
     * inner exception occurs. Can be used for logging
     *
     * @param th  failure cause
     */
    default void onInternalException(@NotNull Throwable th) {
    }

}
