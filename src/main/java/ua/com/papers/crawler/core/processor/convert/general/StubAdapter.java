package ua.com.papers.crawler.core.processor.convert.general;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.convert.ElementConverter;
import ua.com.papers.crawler.settings.PageSetting;

import javax.validation.constraints.NotNull;

/**
 * Just returns element as it was passed without
 * modifications
 */
public final class StubAdapter implements ElementConverter<Element> {

    private static final class Holder {
        private static final StubAdapter INSTANCE = new StubAdapter();
    }

    public static StubAdapter getInstance() {
        return Holder.INSTANCE;
    }

    private StubAdapter() {
    }

    @Override
    public Class<? extends Element> converts() {
        return Element.class;
    }

    @Override
    public Element convert(@NotNull Element i, Page page, PageSetting settings) {
        return i;
    }
}
