package ua.com.papers.crawler.core.factory;

import lombok.NonNull;
import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.main.ICrawler;

/**
 * Created by Максим on 11/27/2016.
 */
@Validated
public interface ICrawlerFactory {

    @NonNull
    ICrawler create();

}
