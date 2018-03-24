package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.main.vo.PageID;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.settings.v2.Page;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.core.processor.convert.SkipAdapter;
import ua.com.papers.crawler.core.processor.convert.StringAdapter;
import ua.com.papers.crawler.core.processor.exception.ProcessException;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.services.crawler.UrlAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Component
public final class AnnotationFormatManager implements OutFormatter {

    private final Map<Class<?>, Converter<?>> adapters;
    private final Map<PageID, ? extends Collection<HandlerInvoker>> idToHandlers;

    @Autowired
    public AnnotationFormatManager(@NonNull @Qualifier("handlers") Collection<?> handlers) {
        Preconditions.checkArgument(!handlers.isEmpty());
        this.adapters = new HashMap<>();
        // register default converters
        registerAdapter(SkipAdapter.instance);
        registerAdapter(StringAdapter.instance);
        registerAdapter(UrlAdapter.INSTANCE);
        this.idToHandlers = mapToHandlers(handlers);
    }

    @Override
    public void registerAdapter(Converter<?> adapter) {
        synchronized (adapters) {
            adapters.put(adapter.converts(), adapter);
        }
    }

    @Override
    public void unregisterAdapter(Class<? extends Converter<?>> cl) {
        synchronized (adapters) {
            val toRemove = adapters.values().stream()
                    .filter(a -> a.getClass().isAssignableFrom(cl)).collect(Collectors.toList());

            //noinspection SuspiciousMethodCalls
            adapters.values().remove(toRemove);
        }
    }

    @Override
    public Set<? extends Converter<?>> getRegisteredAdapters() {
        return Collections.unmodifiableSet(new HashSet<>(adapters.values()));
    }

    @Override
    public void formatPage(PageID pageID, ua.com.papers.crawler.core.main.bo.Page page) throws ProcessException {
        val handlers = idToHandlers.get(pageID);

        if (handlers != null && !handlers.isEmpty()) {

            try {
                processPage(page, handlers);
            } catch (final Throwable e) {
                log.log(Level.SEVERE, String.format("Page handler thrown an exception while handling page %s", page.getUrl()));
                throw new ProcessException(e);
            }
        } else {
            log.log(Level.INFO, String.format("Not found corresponding handler for the page with id %s", pageID));
        }
    }

    private void processPage(ua.com.papers.crawler.core.main.bo.Page page, Iterable<HandlerInvoker> invokers) throws InvocationTargetException, IllegalAccessException {
        for (val handler : invokers) {
            handler.invoke(page);
        }
    }

    private Map<PageID, ? extends Collection<HandlerInvoker>> mapToHandlers(Collection<?> handlers) {
        return handlers.stream().collect(
                Collectors.groupingBy(
                        AnnotationFormatManager::extractPageId,
                        Collectors.mapping(o -> new HandlerInvoker(o, this::getRawTypeConverter, this::getAdapter), Collectors.toList()))
        );
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> getRawTypeConverter(Class<T> cl) {
        synchronized (adapters) {
            return Preconditions.checkNotNull((Converter<T>) adapters.get(cl), String.format("No handler found for %s", cl));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> getAdapter(Class<? extends Converter<T>> cl) {
        synchronized (adapters) {
            val found = adapters.values().stream().filter(adapter -> adapter.getClass().isAssignableFrom(cl)).findFirst();

            return found.map(adapter -> (Converter<T>) adapter)
                    .orElseGet(() -> {
                        val newAdapter = AnnotationFormatManager.<T>constructAdapter(cl);

                        registerAdapter(newAdapter);
                        return newAdapter;
                    });
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

    private static PageID extractPageId(Object o) {
        val handler = Preconditions.checkNotNull(o.getClass().getAnnotation(Page.class),
                String.format("No %s annotation was found for class %s", o.getClass().getName(), Page.class.getName()));

        return new PageID(handler.id());
    }

}
