package ua.com.papers.crawler.core.domain.storage;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * <p>
 *     Class which represents indexed page
 * </p>
 * Created by Максим on 12/27/2016.
 */
@Value
public class Index {

    DateTime lastVisit;
    String contentHash;
    URL url;

    public Index(@NotNull DateTime lastVisit, @NotNull URL url, @NotNull String contentHash) {
        this.lastVisit = Preconditions.checkNotNull(lastVisit);
        this.url = Preconditions.checkNotNull(url);
        this.contentHash = Preconditions.checkNotNull(contentHash);
    }
}
