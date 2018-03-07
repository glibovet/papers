package ua.com.papers.crawler.core.domain.format.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 1/8/2017.
 */
public final class StringAdapter implements IPartAdapter<String> {

    public static final StringAdapter instance = new StringAdapter();

    private StringAdapter() {}

    @Override
    public String convert(@NotNull Element element, Page page) {
        return element.text();
    }
}
