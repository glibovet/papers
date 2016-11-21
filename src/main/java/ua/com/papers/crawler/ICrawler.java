package ua.com.papers.crawler;

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
         * @param th failure cause
         */
        default void onException(@NotNull Throwable th) {}

    }

    /**
     * This method starts crawler
     *
     * @param callback callback to monitor progress status
     */
    void start(@NotNull ICallback callback);

    /**
     * This method starts crawler
     *
     * @param callback callbacks to monitor progress status
     */
    void start(@NotNull Collection<ICallback> callback);

    /**
     * Stops crawler immediately
     */
    void stop();

    /**
     * Waits at most specified amount of millis and finally
     * stops crawler
     * @param wait millis to wait
     */
    void stop(long wait);

}
