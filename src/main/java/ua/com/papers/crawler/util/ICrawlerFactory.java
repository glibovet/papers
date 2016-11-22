package ua.com.papers.crawler.util;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 11/27/2016.
 */
@Validated
@Service
public interface ICrawlerFactory {

    /**
     * Creates crawler from settings
     *
     * @param settings settings to be used while creating crawler
     * @return new instance of crawler
     */
    ICrawler create(@NotNull Settings settings);

}
