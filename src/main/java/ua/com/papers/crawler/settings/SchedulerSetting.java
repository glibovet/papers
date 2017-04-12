package ua.com.papers.crawler.settings;

import lombok.Value;

/**
 * Created by Максим on 11/27/2016.
 */
@Value
public class SchedulerSetting {

    public static final long MIN_DELAY = 0L;

    int indexThreads;
    int processingThreads;
    long indexDelay;
    long processingDelay;

    @lombok.Builder(builderClassName = "Builder")
    private SchedulerSetting(int processingThreads, int indexThreads, long indexDelay, long processingDelay) {
        Conditions.checkArgument(processingThreads >= 1,
                String.format("invalid processing threads num, was %d", processingThreads));
        Conditions.checkArgument(indexThreads >= 1,
                String.format("invalid indexing threads num, was %d", indexThreads));

        this.indexDelay = toDelay(indexDelay);
        this.processingDelay = toDelay(processingDelay);
        this.processingThreads = processingThreads;
        this.indexThreads = indexThreads;
    }

    private static long toDelay(long original) {
        return original < MIN_DELAY ? MIN_DELAY : original;
    }

}
