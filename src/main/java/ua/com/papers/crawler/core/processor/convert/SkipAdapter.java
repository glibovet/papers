package ua.com.papers.crawler.core.processor.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * Just returns element as it was passed without
 * modifications
 */
public class SkipAdapter implements Converter<Element> {

    public static final SkipAdapter instance = new SkipAdapter();

    private SkipAdapter() {
    }

    @Override
    public Class<? extends Element> converts() {
        return Element.class;
    }

    @Override
    public Element convert(@NotNull Element element, Page page) {
        return element;
    }
}
