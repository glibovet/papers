package ua.com.papers.crawler.core.processor.annotation;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.convert.Converter;

public final class StubCallAdapter implements Converter<Page> {

    private static final class Holder {
        private static final StubCallAdapter INSTANCE = new StubCallAdapter();
    }

    private StubCallAdapter() {
    }

    public static StubCallAdapter getInstance() {
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
