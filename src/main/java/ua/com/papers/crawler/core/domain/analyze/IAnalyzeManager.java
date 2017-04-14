package ua.com.papers.crawler.core.domain.analyze;

import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * <p>
 * Class which allows to analyze web-pages
 * </p>
 * Created by Максим on 12/18/2016.
 */
public interface IAnalyzeManager {

    /**
     * Analyzes page
     *
     * @param page page to analyze
     * @return instance of {@linkplain Result} if web page matches criteria, or null
     * in another case
     */
    @NotNull
    Set<Result> analyze(@NotNull Page page);

}
