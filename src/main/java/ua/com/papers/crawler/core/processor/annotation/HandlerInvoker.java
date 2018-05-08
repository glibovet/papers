package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.var;
import lombok.val;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v2.process.AfterPage;
import ua.com.papers.crawler.settings.v2.process.BeforePage;
import ua.com.papers.crawler.settings.v2.process.Binding;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

final class HandlerInvoker implements Invoker {

    private final Collection<Invoker> invokers;

    HandlerInvoker(@NonNull Object handler, @NonNull Context context) {

        val mapper = new Mapper(handler, context);

        this.invokers = new ArrayList<>(mapper.getBeforeInvokers());

        val groupInvokers = mapper.getProcessInvokers().stream()
                // Map<Int, List<ProcessInvoker>>
                .collect(Collectors.groupingBy(i -> i.getHandles().group(), Collectors.toList()))
                // Map<Int, SingleMethodExecutor>
                .entrySet().stream()
                .map(e -> new SingleGroupInvoker(e.getValue()))
                .collect(Collectors.toList());

        invokers.addAll(groupInvokers);
        invokers.addAll(mapper.getBindingInvokers());
        invokers.addAll(mapper.getAfterInvokers());
    }

    @Override
    public void invoke(Page page) throws InvocationTargetException, IllegalAccessException {
        for (val invoker : invokers) {
            invoker.invoke(page);
        }
    }

}


@Value
final class Mapper {

    private final Collection<Invoker> beforeInvokers;
    private final Collection<Invoker> afterInvokers;
    private final Collection<ProcessInvoker> processInvokers;
    private final Collection<Invoker> bindingInvokers;

    Mapper(Object handler, Context context) {

        val beforeInvokers = new ArrayList<Invoker>(1);
        val afterInvokers = new ArrayList<Invoker>(1);
        val processInvokers = new ArrayList<ProcessInvoker>(1);
        val bindingInvokers = new ArrayList<Invoker>(1);

        for (val method : handler.getClass().getMethods()) {

            if (method.getDeclaringClass() == Object.class) {
                continue;
            }

            if (method.isAnnotationPresent(BeforePage.class)) {
                beforeInvokers.add(new LifecycleInvoker(method, handler, BeforePage.class));

            } else if (method.isAnnotationPresent(AfterPage.class)) {
                afterInvokers.add(new LifecycleInvoker(method, handler, AfterPage.class));

            } else if (method.isAnnotationPresent(Handles.class)) {
                processInvokers.add(new ProcessInvoker(method, handler, context));

            } else if (hasArgumentBindings(method)) {
                bindingInvokers.add(new BindingInvoker(method, handler, context));
            }
        }

        this.beforeInvokers = Collections.unmodifiableCollection(beforeInvokers);
        this.afterInvokers = Collections.unmodifiableCollection(afterInvokers);
        this.processInvokers = Collections.unmodifiableCollection(processInvokers);
        this.bindingInvokers = Collections.unmodifiableCollection(bindingInvokers);
    }

    private static boolean hasArgumentBindings(Method method) {
        val annotations = method.getParameterAnnotations();

        for (var i = 0; i < annotations.length; ++i) {
            for (var j = 0; j < annotations[i].length; ++i) {
                if (annotations[i][j].annotationType().equals(Binding.class)) {
                    return true;
                }
            }
        }

        return false;
    }

}