package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.processor.convert.CollectionConverter;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.core.processor.convert.ElementConverter;
import ua.com.papers.crawler.util.Preconditions;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
public final class Context {

    private final Map<Class<?>, Converter<?, ?>> adapters;
    // collection -> param type -> converter
    private final Map<Class<?>, Map<Class<?>, CollectionConverter<?, ?>>> collectionsAdapters;
    // collection -> fabric function
    private final Map<Class<?>, Function<ElementConverter<?>, ? extends CollectionConverter<?, ?>>> collectionProviders;

    Context(@NonNull Collection<ElementConverter<?>> adapters) {
        this.adapters = adapters.stream().collect(Collectors.toMap(Converter::converts, v -> v));
        this.collectionProviders = new HashMap<>();
        this.collectionsAdapters = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public <R> ElementConverter<R> getRawTypeConverter(Class<R> cl) {
        synchronized (adapters) {
            return Preconditions.checkNotNull((ElementConverter<R>) adapters.get(cl), String.format("No handler found for %s", cl));
        }
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public synchronized <I, E extends Element, C extends Collection<? extends E>> CollectionConverter<E, C> getCollectionTypeConverter(Class<C> collection, Class<I> elem) {
        return (CollectionConverter<E, C>) collectionsAdapters.computeIfAbsent(collection, computeCollectionConverter(elem)).get(elem);
    }

    private <I> Function<Class<?>, Map<Class<?>, CollectionConverter<?, ?>>> computeCollectionConverter(Class<I> elem) {
        return collectionClass -> {
            val map = new HashMap<Class<?>, CollectionConverter<?, ?>>();
            val providerFun = Preconditions.checkNotNull(collectionProviders.get(collectionClass), "Not found collection provider of type %s", collectionClass);

            map.put(elem, providerFun.apply(getRawTypeConverter(elem)));
            return map;
        };
    }

    @SuppressWarnings("unchecked")
    public <T, R> Converter<T, R> getAdapter(Class<? extends Converter<T, R>> cl) {
        synchronized (adapters) {
            val found = adapters.values().stream().filter(adapter -> adapter.getClass().isAssignableFrom(cl)).findFirst();

            return found.map(adapter -> (Converter<T, R>) adapter)
                    .orElseGet(() -> {
                        val newAdapter = Context.<T, R>constructAdapter(cl);

                        registerAdapter(newAdapter);
                        return newAdapter;
                    });
        }
    }

    public Set<? extends Converter<?, ?>> getRegisteredAdapters() {
        return Collections.unmodifiableSet(new HashSet<>(adapters.values()));
    }

    void registerAdapter(Converter<?, ?> adapter) {
        synchronized (adapters) {
            adapters.put(adapter.converts(), adapter);
        }
    }

    synchronized <C extends Collection<?>> void registerAdapterProvider(@NonNull Class<C> collectionClass,
                                                                        @NonNull Function<ElementConverter<?>, CollectionConverter<?, C>> provider) {
        collectionProviders.put(collectionClass, provider);
    }

    void unregisterAdapter(Class<? extends Converter<?, ?>> cl) {
        synchronized (adapters) {
            val toRemove = adapters.values().stream()
                    .filter(a -> a.getClass().isAssignableFrom(cl)).collect(Collectors.toList());

            //noinspection SuspiciousMethodCalls
            adapters.values().remove(toRemove);
        }
    }

    private static <T, R> Converter<T, R> constructAdapter(Class<? extends Converter<T, R>> cl) {
        try {
            return cl.getConstructor().newInstance();
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(String.format("%s should define non-arg constructor", cl), e);
        } catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(String.format("An error occurred while creating a new instance of %s", cl), e);
        }
    }

}
