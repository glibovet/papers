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

    public static final long NO_DELAY = 0L;

    private ScheduledExecutorService executorService;
    private long startupDelay;
    private long indexDelay;

    private SchedulerSetting(@Nullable ScheduledExecutorService executorService, long startupDelay, long indexDelay) {
        this.executorService = executorService == null ? defaultExecutor() : executorService;
        this.startupDelay = startupDelay;
        this.indexDelay = indexDelay;
    }

    private static ScheduledExecutorService defaultExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

}
