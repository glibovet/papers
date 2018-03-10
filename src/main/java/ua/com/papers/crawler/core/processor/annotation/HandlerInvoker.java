package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.settings.v2.process.AfterPage;
import ua.com.papers.crawler.settings.v2.process.BeforePage;
import ua.com.papers.crawler.settings.v2.process.Handles;
import ua.com.papers.crawler.core.processor.convert.Converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

final class HandlerInvoker implements Invoker {
    private final Collection<? extends Invoker> beforeInvokers;
    private final Collection<? extends Invoker> afterInvokers;
    private final Map<Integer, ProcessInvokeExecutor> processInvokers;

    <T> HandlerInvoker(@NonNull Object handler, @NonNull Function<Class<T>, Converter<T>> rawTypeAdapterSupplier,
                   @NonNull Function<Class<? extends Converter<T>>, Converter<T>> adapterSupplier) {
        val mapped = mapMethods(handler, rawTypeAdapterSupplier, adapterSupplier);

        this.beforeInvokers = mapped.get(BeforePage.class);
        this.afterInvokers = mapped.get(AfterPage.class);
        // cast to process invokers
        this.processInvokers = mapped.get(Handles.class).stream().map(i -> (ProcessInvoker) i)
                // Map<Int, List<ProcessInvoker>>
                .collect(Collectors.groupingBy(i -> i.getHandles().group(), Collectors.toList()))
                // Map<Int, ProcessInvokeExecutor>
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> new ProcessInvokeExecutor(v.getValue())));
    }

    @Override
    public void invoke(Page page) throws InvocationTargetException, IllegalAccessException {
        for (val before : beforeInvokers) {
            before.invoke(page);
        }

        for (val entry : processInvokers.entrySet()) {
            entry.getValue().invoke(page);
        }

        for (val after : afterInvokers) {
            after.invoke(page);
        }
    }

    private <T> Map<Class<? extends Annotation>, ? extends Collection<Invoker>>
    mapMethods(Object handler, Function<Class<T>, Converter<T>> rawTypeAdapterSupplier,
               @NonNull Function<Class<? extends Converter<T>>, Converter<T>> adapterSupplier) {

        val map = new HashMap<Class<? extends Annotation>, ArrayList<Invoker>>() {
            {
                put(BeforePage.class, new ArrayList<>(1));
                put(AfterPage.class, new ArrayList<>(1));
                put(Handles.class, new ArrayList<>(1));
            }
        };

        @Value
        class Remapper implements BiFunction<Class<? extends Annotation>, ArrayList<Invoker>, ArrayList<Invoker>> {
            Method method;
            Object target;

            @Override
            public ArrayList<Invoker> apply(Class<? extends Annotation> aClass, ArrayList<Invoker> methods) {
                if (aClass == Handles.class) {
                    methods.add(new ProcessInvoker(method, target, rawTypeAdapterSupplier, adapterSupplier));
                } else {
                    methods.add(new LifecycleInvoker(method, target, aClass));
                }
                return methods;
            }
        }

        for (val method : handler.getClass().getMethods()) {

            if (method.isAnnotationPresent(BeforePage.class)) {
                map.compute(BeforePage.class, new Remapper(method, handler));

            } else if (method.isAnnotationPresent(AfterPage.class)) {
                map.compute(AfterPage.class, new Remapper(method, handler));

            } else if (method.isAnnotationPresent(Handles.class)) {
                map.compute(Handles.class, new Remapper(method, handler));
            }
        }

        map.values().forEach(ArrayList::trimToSize);
        return map;
    }

}
