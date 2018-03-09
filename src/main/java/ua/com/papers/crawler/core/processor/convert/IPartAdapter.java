package ua.com.papers.crawler.core.processor.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * Class which transforms {@linkplain Element} into desired data type
 * </p>
 * <p>
 * In order to be created via reflection a no-args constructor should be supplied, in another case this adapter should
 * be registered via {@linkplain ua.com.papers.crawler.core.processor.IFormatManager#registerAdapter(IPartAdapter)}
 * </p>
 * Created by Максим on 1/8/2017.
 */
public interface IPartAdapter<T> {

    /**
     * Converts {@linkplain Element} into T
     *
     * @param element element to convert
     * @param page    page to format
     * @return transformed instance of T, may be null
     */
    @NotNull
    T convert(@NotNull Element element, @NotNull Page page);

}
