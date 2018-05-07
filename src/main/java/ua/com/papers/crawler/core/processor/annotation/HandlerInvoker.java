package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.settings.v2.process.AfterPage;
import ua.com.papers.crawler.settings.v2.process.BeforePage;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

final class HandlerInvoker implements Invoker {

    private final Collection<? extends Invoker> beforeInvokers;
    private final Collection<? extends Invoker> afterInvokers;
    private final Collection<? extends SingleGroupInvoker> groupInvokers;

    <T> HandlerInvoker(@NonNull Object handler, @NonNull Function<Class<T>, Converter<T>> rawTypeAdapterSupplier,
                       @NonNull Function<Class<? extends Converter<T>>, Converter<T>> adapterSupplier) {

        val mapper = new Mapper<T>(handler, rawTypeAdapterSupplier, adapterSupplier);

        this.beforeInvokers = mapper.getBeforeInvokers();
        this.afterInvokers = mapper.getAfterInvokers();
        this.groupInvokers = mapper.getProcessInvokers().stream()
                // Map<Int, List<ProcessInvoker>>
                .collect(Collectors.groupingBy(i -> i.getHandles().group(), Collectors.toList()))
                // Map<Int, SingleMethodExecutor>
                .entrySet().stream()
                .map(e -> new SingleGroupInvoker(e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public void invoke(Page page) throws InvocationTargetException, IllegalAccessException {
        for (val before : beforeInvokers) {
            before.invoke(page);
        }

        for (val invoker : groupInvokers) {
            invoker.invoke(page);
        }

        for (val after : afterInvokers) {
            after.invoke(page);
        }
    }

}


@Value
final class Mapper<T> {

    private final Collection<Invoker> beforeInvokers;
    private final Collection<Invoker> afterInvokers;
    private final Collection<ProcessInvoker> processInvokers;

    Mapper(Object handler, Function<Class<T>, Converter<T>> rawTypeAdapterSupplier,
           @NonNull Function<Class<? extends Converter<T>>, Converter<T>> adapterSupplier) {

        val beforeInvokers = new ArrayList<Invoker>(1);
        val afterInvokers = new ArrayList<Invoker>(1);
        val processInvokers = new ArrayList<ProcessInvoker>(1);

        for (val method : handler.getClass().getMethods()) {

            if (method.isAnnotationPresent(BeforePage.class)) {
                beforeInvokers.add(new LifecycleInvoker(method, handler, BeforePage.class));

            } else if (method.isAnnotationPresent(AfterPage.class)) {
                afterInvokers.add(new LifecycleInvoker(method, handler, AfterPage.class));

            } else if (method.isAnnotationPresent(Handles.class)) {
                processInvokers.add(new ProcessInvoker(method, handler, rawTypeAdapterSupplier, adapterSupplier));
            }
        }

        this.beforeInvokers = Collections.unmodifiableCollection(beforeInvokers);
        this.afterInvokers = Collections.unmodifiableCollection(afterInvokers);
        this.processInvokers = Collections.unmodifiableCollection(processInvokers);
    }
}