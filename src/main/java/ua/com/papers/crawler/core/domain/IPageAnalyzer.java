package ua.com.papers.crawler.core.domain;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/1/2016.
 */
@Service
@Validated
public interface IPageAnalyzer {

    /**
     * Analyzes single page
     *
     * @param page page to analyze
     * @return true if web page matches criteria
     */
    boolean matches(@NotNull Page page);

}
