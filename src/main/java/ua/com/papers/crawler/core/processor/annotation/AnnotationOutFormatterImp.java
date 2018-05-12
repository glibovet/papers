package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.main.vo.PageID;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.annotation.invocation.HandlerInvoker;
import ua.com.papers.crawler.core.processor.convert.*;
import ua.com.papers.crawler.core.processor.exception.ProcessException;
import ua.com.papers.crawler.settings.v2.PageHandler;
import ua.com.papers.crawler.util.Preconditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Component
public final class AnnotationOutFormatterImp implements OutFormatter {

    private final Context context;
    private final Map<PageID, ? extends Collection<HandlerInvoker>> idToHandlers;

    @Autowired
    public AnnotationOutFormatterImp(@NonNull @Qualifier("handlers") Collection<?> handlers) {
        Preconditions.checkArgument(!handlers.isEmpty());
        // register default converters
        this.context = new Context(Arrays.asList(
                StubAdapter.getInstance(),
                StringAdapter.getInstance(),
                UrlAdapter.getInstance(),
                PageAdapter.getInstance()
        ));
        this.idToHandlers = mapToHandlers(handlers);
    }

    @Override
    public void registerAdapter(Converter<?> adapter) {
        context.registerAdapter(adapter);
    }

    @Override
    public void unregisterAdapter(Class<? extends Converter<?>> cl) {
        context.unregisterAdapter(cl);
    }

    @Override
    public Set<? extends Converter<?>> getRegisteredAdapters() {
        return context.getRegisteredAdapters();
    }

    @Override
    public void formatPage(PageID pageID, Page page) throws ProcessException {
        val handlers = idToHandlers.get(pageID);

        if (handlers != null && !handlers.isEmpty()) {

            try {
                handlers.forEach(h -> h.invoke(page));
            } catch (final Throwable e) {
                log.log(Level.SEVERE, String.format("Page handler thrown an exception while handling page %s", page.getUrl()));
                throw new ProcessException(e);
            }
        } else {
            log.log(Level.INFO, String.format("Not found corresponding handler for the page with id %s", pageID));
        }
    }

    private Map<PageID, ? extends Collection<HandlerInvoker>> mapToHandlers(Collection<?> handlers) {
        return handlers.stream().collect(
                Collectors.groupingBy(
                        AnnotationOutFormatterImp::extractPageId,
                        Collectors.mapping(o -> new HandlerInvoker(o, context), Collectors.toList()))
        );
    }

    private static PageID extractPageId(Object o) {
        val handler = Preconditions.checkNotNull(o.getClass().getAnnotation(PageHandler.class),
                String.format("No %s annotation was found for class %s", PageHandler.class.getName(), o.getClass().getName()));

        return new PageID(handler.id());
    }

}
