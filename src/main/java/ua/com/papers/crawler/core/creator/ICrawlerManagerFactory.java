package ua.com.papers.crawler.core.creator;

import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;

/**
 * <p>
 * Interface which allows to
 * create {@linkplain ICrawlerManager} instance.
 * Classes which implements this interface are
 * usually anchored to some properties format
 * </p>
 * Created by Максим on 1/9/2017.
 */
public interface ICrawlerManagerFactory {

    /**
     * @return a new instance of {@linkplain ICrawlerManager}
     */
    ICrawlerManager create();

}
