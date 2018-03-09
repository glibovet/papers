package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.process.AfterPage;
import ua.com.papers.crawler.core.processor.annotation.process.BeforePage;
import ua.com.papers.crawler.core.processor.annotation.process.OnHandle;
import ua.com.papers.crawler.core.processor.convert.IPartAdapter;

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

final class HandlerInvoker implements Invokeable {
    private final Collection<? extends Invokeable> beforeInvokers;
    private final Collection<? extends Invokeable> afterInvokers;
    private final Map<Integer, ProcessInvokeExecutor> processInvokers;

    HandlerInvoker(@NonNull Object handler, @NonNull Function<Class<?>, IPartAdapter<?>> supplier) {
        val mapped = mapMethods(handler, supplier);

        this.beforeInvokers = mapped.get(BeforePage.class);
        this.afterInvokers = mapped.get(AfterPage.class);
        // cast to process invokers
        this.processInvokers = mapped.get(OnHandle.class).stream().map(i -> (ProcessInvoker) i)
                // Map<Int, List<ProcessInvoker>>
                .collect(Collectors.groupingBy(i -> i.getOnHandle().group(), Collectors.toList()))
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

    private Map<Class<? extends Annotation>, ? extends Collection<Invokeable>> mapMethods(Object handler, Function<Class<?>, IPartAdapter<?>> supplier) {
        val map = new HashMap<Class<? extends Annotation>, ArrayList<Invokeable>>() {
            {
                put(BeforePage.class, new ArrayList<>(1));
                put(AfterPage.class, new ArrayList<>(1));
                put(OnHandle.class, new ArrayList<>(1));
            }
        };

        @Value
        class Remapper implements BiFunction<Class<? extends Annotation>, ArrayList<Invokeable>, ArrayList<Invokeable>> {
            Method method;
            Object target;

            @Override
            public ArrayList<Invokeable> apply(Class<? extends Annotation> aClass, ArrayList<Invokeable> methods) {
                if (aClass == OnHandle.class) {
                    methods.add(new ProcessInvoker(method, target, supplier));
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

            } else if (method.isAnnotationPresent(OnHandle.class)) {
                map.compute(OnHandle.class, new Remapper(method, handler));
            }
        }

        map.values().forEach(ArrayList::trimToSize);
        return map;
    }

}
