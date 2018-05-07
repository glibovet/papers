package ua.com.papers.crawler.core.main;


import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.Settings;
import ua.com.papers.crawler.settings.v1.PageHandlerV1;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;

/**
 * <p>
 * Represents web crawler contract
 * </p>
 * Created by Максим on 11/27/2016.
 */
public interface ICrawler {

    interface Callback {
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
        default void onCrawlException(@Nullable URL url, @NotNull Throwable th) {
        }

    }

    @NotNull
    Settings getSettings();

    void start(@NotNull Callback callback);

    /**
     * This method starts crawler
     *
     * @param handlers to process page parts, each handler should be annotated with
     *                 {@linkplain PageHandlerV1} or {@linkplain IllegalArgumentException} will be raised
     * @param callback callback to monitor progress status
     * @param urls     start urls to process
     */
    @Deprecated
    void start(@NotNull Callback callback, @NotNull Collection<?> handlers, @NotNull Collection<? extends URL> urls);

    /**
     * Stops crawler. <i>Note, that invocation of this method doesn't guarantee that crawler
     * will shutdown instantly</i>
     */
    void stop();

}
