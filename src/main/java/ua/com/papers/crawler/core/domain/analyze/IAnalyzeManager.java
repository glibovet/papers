package ua.com.papers.crawler.core.domain.analyze;

import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/18/2016.
 */
public interface IAnalyzeManager {

    /**
     * Analyzes page
     *
     * @param page page to analyze
     * @return true if web page matches criteria
     */
    boolean matches(@NotNull Page page);

}
