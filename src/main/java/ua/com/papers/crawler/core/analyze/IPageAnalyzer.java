package ua.com.papers.crawler.core.analyze;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * <p>Single page analyzer contract</p>
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
    @NotNull
    Result analyze(@NotNull Page page);

}
