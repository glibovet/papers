package ua.com.papers.crawler.settings;

import lombok.Value;
import ua.com.papers.crawler.util.Preconditions;

/**
 * Represents scheduling settings such as number of active threads, pause between accesses to a web-resource
 */
@Value
public class SchedulerSetting {

    public static final long MIN_DELAY = 0L;

    int indexThreads;
    int processingThreads;
    long indexDelay;
    long processingDelay;

    @lombok.Builder
    public SchedulerSetting(int processingThreads, int indexThreads, long indexDelay, long processingDelay) {
        Preconditions.checkArgument(processingThreads >= 1,
                "invalid processing threads num, was %d", processingThreads);
        Preconditions.checkArgument(indexThreads >= 1,
                "invalid indexing threads num, was %d", indexThreads);

        this.indexDelay = toDelay(indexDelay);
        this.processingDelay = toDelay(processingDelay);
        this.processingThreads = processingThreads;
        this.indexThreads = indexThreads;
    }

    private static long toDelay(long value) {
        return value < MIN_DELAY ? MIN_DELAY : value;
    }

}
