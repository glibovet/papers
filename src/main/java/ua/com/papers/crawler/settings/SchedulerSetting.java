package ua.com.papers.crawler.settings;

import lombok.Value;

/**
 * Created by Максим on 11/27/2016.
 */
@Value
public class SchedulerSetting {

    public static final long MIN_DELAY = 0L;

    boolean isSeparatedIndexing;
    int threads;
    long startupDelay;
    long indexDelay;

    @lombok.Builder(builderClassName = "Builder")
    private SchedulerSetting(int threads, long startupDelay, long indexDelay, boolean isSeparatedIndex) {
        this.threads = threads;
        this.startupDelay = toDelay(startupDelay);
        this.indexDelay = toDelay(indexDelay);
        this.isSeparatedIndexing = isSeparatedIndex;
    }

    private static long toDelay(long original) {
        return original < MIN_DELAY ? MIN_DELAY : original;
    }

}
