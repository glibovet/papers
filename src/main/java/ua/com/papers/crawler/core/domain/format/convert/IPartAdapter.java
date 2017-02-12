package ua.com.papers.crawler.core.domain.format.convert;

import org.jsoup.nodes.Element;

import javax.annotation.Nullable;
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
     * @param element element to convert
     * @return transformed instance of T, may be null
     */
    @Nullable
    T convert(@NotNull Element element);

}
