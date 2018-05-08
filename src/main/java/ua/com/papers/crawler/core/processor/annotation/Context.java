package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.util.Preconditions;

import java.util.HashMap;
import java.util.Map;

@Value
final class Context {

    private final Map<Class<?>, Converter<?>> adapters;

    Context(@NonNull Map<Class<?>, Converter<?>> adapters) {
        this.adapters = new HashMap<>(adapters);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public <T> Converter<T> getRawTypeConverter(Class<T> cl) {
        return Preconditions.checkNotNull((Converter<T>) adapters.get(cl), String.format("No handler found for %s", cl));
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public <T> Converter<T> getAdapter(Class<? extends Converter<T>> cl) {
        val found = adapters.values().stream().filter(adapter -> adapter.getClass().isAssignableFrom(cl)).findFirst();

        return found.map(adapter -> (Converter<T>) adapter)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Couldn't find type adapter compatible with %s", cl)));
    }

}
