package ua.com.papers.crawler.settings;

import lombok.Builder;
import lombok.Value;

/**
 * Created by Максим on 11/27/2016.
 */
@Value
@Builder(builderClassName = "Builder")
public class SchedulerSetting {

    public static final long MIN_DELAY = 0L;

    //ScheduledExecutorService executorService;
    int threads;
    long startupDelay;
    long indexDelay;

    private SchedulerSetting(int threads, long startupDelay, long indexDelay) {
        this.threads = threads;
        this.startupDelay = toDelay(startupDelay);
        this.indexDelay = toDelay(indexDelay);
    }

    private static long toDelay(long original) {
        return original < MIN_DELAY ? MIN_DELAY : original;
    }

}
