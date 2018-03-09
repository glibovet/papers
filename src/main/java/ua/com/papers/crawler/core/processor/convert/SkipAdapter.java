package ua.com.papers.crawler.core.processor.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * Just returns element as it was passed without
 * modifications
 */
public class SkipAdapter implements IPartAdapter<Element> {

    public static final SkipAdapter instance = new SkipAdapter();

    private SkipAdapter() {
    }

    @Override
    public Element convert(@NotNull Element element, Page page) {
        return element;
    }
}
