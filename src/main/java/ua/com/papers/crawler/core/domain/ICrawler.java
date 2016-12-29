package ua.com.papers.crawler.core.domain;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.util.PageHandler;

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
@Service
@Validated
public interface ICrawler {

    interface ICallback {
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
        void onPageAccepted(@NotNull Page page);

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
         * @param th failure cause
         */
        default void onException(@NotNull Throwable th) {
        }

    }

    /**
     * This method starts crawler
     *
     * @param handlers to process page parts, each handler should be annotated with
     *                 {@linkplain PageHandler} or {@linkplain IllegalArgumentException} will be raised
     * @param callback callback to monitor progress status
     * @param urls     start urls to process
     */
    void start(@Nullable ICallback callback, @NotNull Collection<Object> handlers, @NotNull Collection<URL> urls);

    /**
     * Stops crawler immediately
     */
    void stop();

}
