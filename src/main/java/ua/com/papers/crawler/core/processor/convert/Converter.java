package ua.com.papers.crawler.core.processor.convert;

import lombok.NonNull;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.OutFormatter;

/**
 * <p>
 * Class which transforms {@linkplain Element} into desired data type
 * </p>
 * <p>
 * In order to be created via reflection a no-args constructor should be supplied, in another case this adapter should
 * be registered via {@linkplain OutFormatter#registerAdapter(Converter)}
 * </p>
 * Created by Максим on 1/8/2017.
 */
public interface Converter<T> {

    @NonNull
    Class<? extends T> converts();

    /**
     * Converts {@linkplain Element} into T
     *
     * @param element element to convert
     * @param page    page to format
     * @return transformed instance of T, may be null
     */
    @NonNull
    T convert(@NonNull Element element, @NonNull Page page);

}
