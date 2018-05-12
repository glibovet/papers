package ua.com.papers.crawler.core.processor.convert;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;

public final class PageAdapter implements Converter<Page> {

    private static final class Holder {
        private static final PageAdapter INSTANCE = new PageAdapter();
    }

    private PageAdapter() {
    }

    public static PageAdapter getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public Class<? extends Page> converts() {
        return Page.class;
    }

    @Override
    public Page convert(Element element, Page page) {
        return page;
    }
}
