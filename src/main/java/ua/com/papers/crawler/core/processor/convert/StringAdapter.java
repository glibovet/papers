package ua.com.papers.crawler.core.processor.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 1/8/2017.
 */
public final class StringAdapter implements Converter<String> {

    public static final StringAdapter instance = new StringAdapter();

    private StringAdapter() {}

    @Override
    public Class<? extends String> converts() {
        return String.class;
    }

    @Override
    public String convert(@NotNull Element element, Page page) {
        return element.text();
    }
}
