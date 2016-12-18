package ua.com.papers.crawler.core.domain.analyze;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;

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

    @NotNull
    PageID getAnalyzeID();

}
