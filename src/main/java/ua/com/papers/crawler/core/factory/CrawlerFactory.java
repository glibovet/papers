package ua.com.papers.crawler.core.factory;

import lombok.NonNull;
import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.main.Crawler;

/**
 * Abstract factory contract to create instances of {@linkplain Crawler}
 */
@Validated
public interface CrawlerFactory {

    @NonNull
    Crawler create();

}
