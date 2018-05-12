package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.ToString;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.util.Preconditions;

@ToString
public final class CallAdapter<T> implements Converter<T> {
    private final Converter<T> converter;

    // try to find adapter for a method argument type
    public CallAdapter(@NonNull Class<?> parameter, @NonNull Context context) {
        converter = Preconditions.checkNotNull((Converter<T>) context.getRawTypeConverter(parameter),
                "Wasn't found adapter for a type %s", parameter);

        validateOrThrow(converter, parameter);
    }

    // explicit adapter was supplied, use it
    public CallAdapter(@NonNull Class<?> parameter, @NonNull Context context, @NonNull Class<? extends Converter<T>> converterClass) {
        converter = Preconditions.checkNotNull(context.getAdapter(converterClass),
                "Wasn't found adapter for explicit adapter %s, method %s", converterClass);

        validateOrThrow(converter, parameter);
    }

    @Override
    public Class<? extends T> converts() {
        return converter.converts();
    }

    @Override
    public T convert(Element element, Page page) {
        return converter.convert(element, page);
    }

    private static void validateOrThrow(Converter<?> converter, Class<?> parameter) {
        Preconditions.checkArgument(parameter.isAssignableFrom(converter.converts()),
                "Converter %s may not be used for parameter of type %s", converter.getClass(), parameter);
    }

}
