package ua.com.papers.crawler.settings;

import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Максим on 11/27/2016.
 */
@Value
@Builder(builderClassName = "Builder")
public class SchedulerSetting {

    public static final long MIN_DELAY = 0L;

    ScheduledExecutorService executorService;
    long startupDelay;
    long indexDelay;

    private SchedulerSetting(@Nullable ScheduledExecutorService executorService, long startupDelay, long indexDelay) {
        this.executorService = executorService == null ? defaultExecutor() : executorService;
        this.startupDelay = toDelay(startupDelay);
        this.indexDelay = toDelay(indexDelay);
    }

    private static ScheduledExecutorService defaultExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    private static long toDelay(long original) {
        return original < MIN_DELAY ? MIN_DELAY : original;
    }

}
