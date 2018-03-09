package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.core.processor.IFormatManager;
import ua.com.papers.crawler.core.processor.annotation.analyze.Handler;
import ua.com.papers.crawler.core.processor.annotation.util.AnnotationUtil;
import ua.com.papers.crawler.core.processor.convert.IPartAdapter;
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
public final class AnnotationFormatManager implements IFormatManager {

    private final Map<Class<?>, IPartAdapter<?>> adapters;
    private final Map<PageID, ? extends Collection<HandlerInvoker>> idToHandlers;

    public AnnotationFormatManager(@NonNull Collection<Object> handlers) {
        Preconditions.checkArgument(!handlers.isEmpty());

        this.idToHandlers = mapToHandlers(handlers);
        this.adapters = new HashMap<Class<?>, IPartAdapter<?>>() {
            {
                // register default converters
                put(AnnotationUtil.getRawType(SkipAdapter.class), SkipAdapter.instance);
                put(AnnotationUtil.getRawType(StringAdapter.class), StringAdapter.instance);
                put(AnnotationUtil.getRawType(UrlAdapter.class), UrlAdapter.INSTANCE);
            }
        };
    }

    @Override
    public void registerAdapter(IPartAdapter<?> adapter) {
        synchronized (adapters) {
            adapters.put(AnnotationUtil.getRawType(adapter.getClass()), adapter);
        }
    }

    @Override
    public void unregisterAdapter(Class<? extends IPartAdapter<?>> cl) {
        synchronized (adapters) {
            val toRemove = adapters.values().stream()
                    .filter(a -> a.getClass().isAssignableFrom(cl)).collect(Collectors.toList());

            //noinspection SuspiciousMethodCalls
            adapters.values().remove(toRemove);
        }
    }

    @Override
    public Set<? extends IPartAdapter<?>> getRegisteredAdapters() {
        return Collections.unmodifiableSet(new HashSet<>(adapters.values()));
    }

    @Override
    public void processPage(PageID pageID, Page page) throws ProcessException {
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

    private void processPage(Page page, Iterable<HandlerInvoker> invokers) throws InvocationTargetException, IllegalAccessException {
        for (val handler : invokers) {
            handler.invoke(page);
        }
    }

    private Map<PageID, ? extends Collection<HandlerInvoker>> mapToHandlers(Collection<Object> handlers) {
        return handlers.stream().collect(
                Collectors.groupingBy(
                        AnnotationFormatManager::extractPageId,
                        Collectors.mapping(o -> new HandlerInvoker(o, this::getConverter), Collectors.toList()))
        );
    }

    private IPartAdapter<?> getConverter(Class<?> cl) {
        synchronized (adapters) {
            IPartAdapter<?> cached = adapters.get(cl);

            if (cached == null) {
                // such converter wasn't found in cache
                // try load converter through reflection
                try {
                    cached = (IPartAdapter<?>) cl.getConstructor().newInstance();
                    adapters.put(cl, cached);
                } catch (final NoSuchMethodException e) {
                    throw new RuntimeException(String.format("%s should define non-arg constructor", cl), e);
                } catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException(String.format("An error occurred while creating a new instance of %s", cl), e);
                }
            }

            return Preconditions.checkNotNull(cached, String.format("No handler for %s", cl));
        }
    }

    private static PageID extractPageId(Object o) {
        val handler = Preconditions.checkNotNull(o.getClass().getAnnotation(Handler.class),
                String.format("No %s annotation was found for class %s", o.getClass().getName(), Handler.class.getName()));

        return new PageID(handler.id());
    }

}
