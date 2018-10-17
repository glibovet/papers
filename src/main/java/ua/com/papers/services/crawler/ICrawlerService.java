package ua.com.papers.services.crawler;

/**
 * <p>
 *     Represents basic crawler service
 *     contract
 * </p>
 * Created by Максим on 6/8/2017.
 */
public interface ICrawlerService {

    void startCrawling();

    void startIndexing();

    void stopCrawling();

    /*boolean isCrawling();

    void startReIndex();

    void stopReIndex();

    boolean isReIndexing();*/

}
