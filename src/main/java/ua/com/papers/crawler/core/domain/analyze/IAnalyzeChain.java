package ua.com.papers.crawler.core.domain.analyze;


import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/1/2016.
 */
@Service
@Validated
public interface IAnalyzeChain {

    /**
     * @param document document to analyze
     * @return analyzed weight, can be 0 if page doesn't satisfies
     * implementation criteria
     */
    int analyze(@NotNull(message = "cannot analyze null document") Document document);

}
