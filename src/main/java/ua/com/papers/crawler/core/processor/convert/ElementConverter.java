package ua.com.papers.crawler.core.processor.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.processor.OutFormatter;

/**
 * <p>
 * Class which transforms {@linkplain Element} into desired data type
 * </p>
 * <p>
 * In order to be created via reflection a no-args constructor should be supplied, in another case this adapter should
 * be registered via {@linkplain OutFormatter#registerAdapter(ElementConverter)}
 * </p>
 * Created by Максим on 1/8/2017.
 */
public interface ElementConverter<R> extends Converter<Element, R> {
}
