package ua.com.papers.crawler.core.processor.annotation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.util.AnnotationUtil;
import ua.com.papers.crawler.util.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@EqualsAndHashCode
@ToString
final class LifecycleInvoker implements Invokeable {
    private final Method method;
    private final Object target;
    private boolean isPageRequired;

    LifecycleInvoker(@NonNull Method method, @NonNull Object target, @NonNull Class<? extends Annotation> annotation) {
        AnnotationUtil.checkMethodOrThrow(method, target);
        Preconditions.checkArgument(method.isAnnotationPresent(annotation),
                String.format("Missing %s annotation", annotation));

        val params = method.getParameterTypes();

        Preconditions.checkArgument(params.length <= 1,
                "Invalid number of parameters, one or none should be used");

        if (params.length == 1) {
            Preconditions.checkArgument(params[0].isAssignableFrom(Page.class),
                    String.format("Method argument type should be assignable from %s", Page.class));
        }

        this.method = method;
        this.target = target;
        this.isPageRequired = params.length == 1;
    }

    @Override
    public void invoke(Page page) throws InvocationTargetException, IllegalAccessException {
        if (isPageRequired) {
            method.invoke(target, page);
        } else {
            method.invoke(target);
        }
    }
}
