package ua.com.papers.crawler.core.main;

import ua.com.papers.crawler.core.analyze.Result;
import ua.com.papers.crawler.core.main.model.Page;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

public interface Behavior {

    @NotNull
    Iterator<URL> urlIterator();

    /**
     * This callback gets invoked before
     * crawler starts do his job
     */
    void onStart(@NotNull Crawler crawler);

    /**
     * Called each time crawler starts to
     * process a new url
     *
     * @param url url which is being analyzed
     */
    void onUrlEntered(@NotNull URL url, @NotNull Crawler crawler);

    @NotNull
    Set<Result> extractMatchingPages(@NotNull Page page, @NotNull Crawler crawler);

    void onPageMatching(@NotNull Page page, @NotNull Crawler crawler, Set<Result> results);

    void onPageNotMatch(@NotNull Page page, @NotNull Crawler crawler, Set<Result> results);

    void onUrlParsed(@NotNull URL url, @NotNull Crawler crawler);

    /**
     * Called before crawler stops doing his job
     */
    void onStop(@NotNull Crawler crawler);

    /**
     * Called each time crawler fails to perform operation or
     * inner exception occurs. Can be used for logging
     *
     * @param url page's url which caused exception
     * @param th  failure cause
     */
    void onCrawlException(@NotNull URL url, @NotNull Throwable th, @NotNull Crawler crawler);

    /**
     * Called each time crawler fails to perform operation or
     * inner exception occurs. Can be used for logging
     *
     * @param th  failure cause
     */
    void onInternalException(@NotNull Throwable th, @NotNull Crawler crawler);

}
