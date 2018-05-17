package ua.com.papers.crawler.core.main;


import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * Represents web crawler contract
 * </p>
 * Created by Максим on 11/27/2016.
 */
public interface Crawler {

    @NotNull
    Settings getSettings();

    void start(@NotNull CrawlingCallback callback);

    void start(@NotNull IndexingCallback callback);

    /**
     * Stops crawler. <i>Note, that invocation of this method doesn't guarantee that crawler
     * will shutdown instantly</i>
     */
    void stop();

}
