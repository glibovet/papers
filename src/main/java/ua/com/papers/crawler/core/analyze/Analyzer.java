package ua.com.papers.crawler.core.analyze;

import ua.com.papers.crawler.core.main.bo.Page;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * <p>
 * Class which allows to analyze web-pages
 * </p>
 * Created by Максим on 12/18/2016.
 */
public interface Analyzer {

    /**
     * Analyzes page and returns set of analyze results
     *
     * @param page page to analyze
     * @return set of {@linkplain Result}
     */
    @NotNull
    Set<Result> analyze(@NotNull Page page);

}
