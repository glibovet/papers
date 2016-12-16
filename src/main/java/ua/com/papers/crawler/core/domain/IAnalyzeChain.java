package ua.com.papers.crawler.core.domain;


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
     * @param page page to analyze
     * @return analyzed weight, can be 0 if page doesn't satisfies
     * implementation criteria
     */
    int analyze(@NotNull(message = "cannot analyze null page") Page page);

}
