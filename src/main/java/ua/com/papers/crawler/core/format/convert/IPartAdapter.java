package ua.com.papers.crawler.core.format.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * Class which transforms {@linkplain Element}
 * into desired data type
 * </p>
 * Created by Максим on 1/8/2017.
 */
public interface IPartAdapter<T> {

    /**
     * Converts {@linkplain Element} into T
     *
     * @param element  element to convert
     * @param page     page to format
     * @return transformed instance of T, may be null
     */
    @NotNull
    T convert(@NotNull Element element, @NotNull Page page);

}
