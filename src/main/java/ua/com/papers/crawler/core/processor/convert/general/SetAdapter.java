package ua.com.papers.crawler.core.processor.convert.general;

import lombok.NonNull;
import org.jsoup.select.Elements;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.convert.CollectionConverter;
import ua.com.papers.crawler.core.processor.convert.ElementConverter;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transforms element into {@linkplain URL}
 * Created by Максим on 1/8/2017.
 */
public final class SetAdapter<I> implements CollectionConverter<I, Set<I>> {

    private final ElementConverter<I> delegate;

    public SetAdapter(@NonNull ElementConverter<I> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<? extends Set<I>> converts() {
        return (Class<? extends Set<I>>) Collections.EMPTY_SET.getClass();
    }

    @Override
    public Set<I> convert(Elements elements, Page page) {
        return elements.stream().map(e -> delegate.convert(e, page)).collect(Collectors.toSet());
    }

}
