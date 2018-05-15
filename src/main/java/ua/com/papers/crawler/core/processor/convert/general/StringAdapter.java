package ua.com.papers.crawler.core.processor.convert.general;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.convert.ElementConverter;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 1/8/2017.
 */
public final class StringAdapter implements ElementConverter<String> {

    private static final class Holder {
        private static final StringAdapter INSTANCE = new StringAdapter();
    }

    public static StringAdapter getInstance() {
        return Holder.INSTANCE;
    }

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
