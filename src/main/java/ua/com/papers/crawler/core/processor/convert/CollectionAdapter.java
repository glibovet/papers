package ua.com.papers.crawler.core.processor.convert;

import lombok.NonNull;
import org.jsoup.select.Elements;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.settings.PageSetting;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Transforms element into {@linkplain URL}
 * Created by Максим on 1/8/2017.
 */
public final class CollectionAdapter<I> implements CollectionConverter<I, Collection<I>> {

    private final ElementConverter<I> delegate;

    public CollectionAdapter(@NonNull ElementConverter<I> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<? extends Collection<I>> converts() {
        return (Class<? extends Collection<I>>) Collections.EMPTY_LIST.getClass();
    }

    @Override
    public Collection<I> convert(Elements elements, Page page, PageSetting settings) {
        return elements.stream().map(e -> delegate.convert(e, page, settings)).collect(Collectors.toList());
    }

}
