package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.main.model.PageID;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.annotation.invocation.HandlerInvoker;
import ua.com.papers.crawler.core.processor.annotation.util.InvokerUtil;
import ua.com.papers.crawler.core.processor.convert.CollectionAdapter;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.core.processor.convert.general.*;
import ua.com.papers.crawler.core.processor.exception.ProcessException;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.v2.PageHandler;
import ua.com.papers.crawler.util.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

@Log
public final class AnnotationOutFormatterImp implements OutFormatter {

    private final Context context;
    private final Map<PageID, ? extends Collection<HandlerInvoker>> idToHandlers;

    @SuppressWarnings("unchecked")
    public AnnotationOutFormatterImp(@NonNull Collection<?> handlers) {
        Preconditions.checkArgument(!handlers.isEmpty());
        // register default converters
        this.context = new Context(Arrays.asList(
                StubAdapter.getInstance(),
                StringAdapter.getInstance(),
                UrlAdapter.getInstance(),
                PageAdapter.getInstance()
        ));

        context.registerAdapterProvider(Collection.class, c -> new CollectionAdapter(c));
        context.registerAdapterProvider(List.class, c -> new ListAdapter(c));
        context.registerAdapterProvider(Set.class, c -> new SetAdapter(c));

        this.idToHandlers = mapToHandlers(handlers);
    }

    @Override
    public void registerAdapter(Converter<?, ?> adapter) {
        context.registerAdapter(adapter);
    }

    @Override
    public void unregisterAdapter(Class<? extends Converter<?, ?>> cl) {
        context.unregisterAdapter(cl);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Optional<? extends Converter<?, R>> getAdapter(Class<? extends R> cl) {
        return context.getRegisteredAdapters().stream().filter(a -> cl.isAssignableFrom(a.converts()))
                .map(a -> (Converter<?, R>) a).findFirst();
    }

    @Override
    public Set<? extends Converter<?, ?>> getRegisteredAdapters() {
        return context.getRegisteredAdapters();
    }

    @Override
    public void formatPage(PageID pageID, Page page, PageSetting settings) throws ProcessException {
        synchronized (idToHandlers) {
            Optional.ofNullable(idToHandlers.get(pageID)).ifPresent(handlers -> handlers.forEach(h -> h.invoke(page, settings)));
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

        return InvokerUtil.newPageId(handler, o);
    }

}
