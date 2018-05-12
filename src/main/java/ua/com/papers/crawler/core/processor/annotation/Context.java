package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.util.Preconditions;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Value
public final class Context {

    private final Map<Class<?>, Converter<?>> adapters;

    Context(@NonNull Collection<Converter<?>> adapters) {
        this.adapters = adapters.stream().collect(Collectors.toMap(Converter::converts, v -> v));
    }

    Context(@NonNull Map<Class<?>, Converter<?>> adapters) {
        this.adapters = new HashMap<>(adapters);
    }

    @SuppressWarnings("unchecked")
    public <T> Converter<T> getRawTypeConverter(Class<T> cl) {
        synchronized (adapters) {
            return Preconditions.checkNotNull((Converter<T>) adapters.get(cl), String.format("No handler found for %s", cl));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Converter<T> getAdapter(Class<? extends Converter<T>> cl) {
        synchronized (adapters) {
            val found = adapters.values().stream().filter(adapter -> adapter.getClass().isAssignableFrom(cl)).findFirst();

            return found.map(adapter -> (Converter<T>) adapter)
                    .orElseGet(() -> {
                        val newAdapter = Context.<T>constructAdapter(cl);

                        registerAdapter(newAdapter);
                        return newAdapter;
                    });
        }
    }

    public Set<? extends Converter<?>> getRegisteredAdapters() {
        return Collections.unmodifiableSet(new HashSet<>(adapters.values()));
    }

    void registerAdapter(Converter<?> adapter) {
        synchronized (adapters) {
            adapters.put(adapter.converts(), adapter);
        }
    }

    void unregisterAdapter(Class<? extends Converter<?>> cl) {
        synchronized (adapters) {
            val toRemove = adapters.values().stream()
                    .filter(a -> a.getClass().isAssignableFrom(cl)).collect(Collectors.toList());

            //noinspection SuspiciousMethodCalls
            adapters.values().remove(toRemove);
        }
    }

    private static <T> Converter<T> constructAdapter(Class<? extends Converter<T>> cl) {
        try {
            return cl.getConstructor().newInstance();
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(String.format("%s should define non-arg constructor", cl), e);
        } catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(String.format("An error occurred while creating a new instance of %s", cl), e);
        }
    }

}
