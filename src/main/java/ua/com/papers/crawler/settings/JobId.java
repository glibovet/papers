package ua.com.papers.crawler.settings;

import lombok.NonNull;
import lombok.Value;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

/**
 * Represents crawling job. Can be used to separate url processing queue for different
 * web sites or other resources.
 */
@Value
public class JobId {
    String id;

    public JobId(@NonNull String jobId) {
        Preconditions.checkArgument(TextUtils.isNonEmpty(jobId));
        this.id = jobId;
    }
}
