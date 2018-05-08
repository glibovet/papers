package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.settings.v2.process.Handles;
import ua.com.papers.crawler.util.Preconditions;

public final class CallAdapter<T> implements Converter<T> {
    private final Converter<T> converter;

    public CallAdapter(@NonNull Class<? extends Converter<T>> converterClass, @NonNull Class<?> parameter, @NonNull Context context) {
        if (Handles.Stub.class.isAssignableFrom(converterClass)) {
            // try to find adapter for a method argument type
            converter = Preconditions.checkNotNull((Converter<T>) context.getRawTypeConverter(parameter),
                    "Wasn't found adapter for a type %s", parameter);
        } else {
            // explicit adapter was supplied, use it
            converter = Preconditions.checkNotNull(context.getAdapter(converterClass),
                    "Wasn't found adapter for explicit adapter %s, method %s", converterClass);
        }

        Preconditions.checkArgument(parameter.isAssignableFrom(converter.converts()),
                "Converter %s may not be used for parameter of type %s", converter.getClass(), parameter);
    }

    @Override
    public Class<? extends T> converts() {
        return converter.converts();
    }

    @Override
    public T convert(Element element, Page page) {
        return converter.convert(element, page);
    }
}
