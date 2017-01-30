package ua.com.papers.crawler.core.domain;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Set;

/**
 * <p>
 * Implement this interface to supply
 * own crawler stop strategy
 * </p>
 * Created by Максим on 1/7/2017.
 */
public interface ICrawlerPredicate {

    /**
     * Implementation of this method should return true if crawler
     * can continue crawling process and false in another case
     *
     * @param visitedUrls   urls that was already visited by crawler
     * @param acceptedPages number of pages that was accepted
     */
    boolean canRun(@NotNull Set<URL> visitedUrls, int acceptedPages);

}
