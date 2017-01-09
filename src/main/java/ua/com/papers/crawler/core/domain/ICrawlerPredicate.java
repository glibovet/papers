package ua.com.papers.crawler.core.domain;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Set;

/**
 * Created by Максим on 1/7/2017.
 */
public interface ICrawlerPredicate {

    boolean canRun(@NotNull Set<URL> visitedUrls, int acceptedPages);

}
