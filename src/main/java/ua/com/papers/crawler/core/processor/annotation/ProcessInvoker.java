package ua.com.papers.crawler.core.processor.annotation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.var;
import lombok.val;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v2.process.Handles;
import ua.com.papers.crawler.core.processor.annotation.util.AnnotationUtil;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents invoker for a method, annotated with {@linkplain Handles}.
 * It transforms supplied page into acceptable method arguments and invokes this method
 */
@EqualsAndHashCode
@ToString
public final class ProcessInvoker implements Invoker {

    private final Method method;
    private final Object target;
    private final Handles handles;
    private final Converter<?> converter;
    private final int auxiliaryArgIndex;

    <T> ProcessInvoker(@NonNull Method method, @NonNull Object target,
                       @NonNull Function<Class<T>, Converter<T>> rawTypeAdapterSupplier,
                       @NonNull Function<Class<? extends Converter<T>>, Converter<T>> adapterSupplier) {

        AnnotationUtil.checkMethodOrThrow(method, target);

        val process = Preconditions.checkNotNull(method.getAnnotation(Handles.class),
                String.format("Missing %s annotation", Annotation.class));

        for (val css : process.selectors()) {
            Preconditions.checkArgument(!TextUtils.isEmpty(css), "Invalid css selector");
        }

        val params = method.getParameterTypes();

        Preconditions.checkArgument(params.length == 2 || params.length == 1,
                "Invalid number of parameters, two or one should be used");

        Class<?> transformArg = params[0];
        var auxiliaryArgIndex = -1;

        if (params.length == 2) {
            val isPageAssignable = params[0].isAssignableFrom(Page.class);

            Preconditions.checkArgument(isPageAssignable || params[1].isAssignableFrom(Page.class),
                    String.format("Auxiliary method's %s argument should accept argument of type %s", method, Page.class));

            auxiliaryArgIndex = isPageAssignable ? 0 : 1;
            transformArg = params[(auxiliaryArgIndex + 1) % 2];
        }

        final Converter<?> converter;

        if (process.converter() == Handles.Stub.class) {
            // guess adapter for a method argument type
            converter = Preconditions.checkNotNull(rawTypeAdapterSupplier.apply((Class<T>) transformArg),
                    String.format("Wasn't found adapter for a type %s, method %s", transformArg, method));
        } else {
            // explicit adapter was supplied, use it
            converter = Preconditions.checkNotNull(adapterSupplier.apply((Class<? extends Converter<T>>) process.converter()),
                    String.format("Wasn't found adapter for explicit adapter %s, method %s", process.converter(), method));
        }

        Preconditions.checkArgument(transformArg.isAssignableFrom(converter.converts()),
                String.format("Converter %s may not be used for an argument of type %s in method %s", converter.getClass(),
                        transformArg, method));

        this.method = method;
        this.target = target;
        this.handles = process;
        this.converter = converter;
        this.auxiliaryArgIndex = auxiliaryArgIndex;
    }

    @NonNull
    public Handles getHandles() {
        return handles;
    }

    @Override
    public void invoke(@NonNull Page page) throws InvocationTargetException, IllegalAccessException {
        for (val css : handles.selectors()) {

            val elements = page.toDocument().select(css).stream()
                    .map(e -> converter.convert(e, page)).collect(Collectors.toList());

            for (val arg : elements) {
                method.invoke(target, wrapArgs(arg, page));
            }
        }
    }

    private Object[] wrapArgs(Object mainArg, Page page) {
        if (auxiliaryArgIndex < 0) {
            return new Object[]{mainArg};
        }

        val args = new Object[2];

        args[auxiliaryArgIndex] = page;
        args[(auxiliaryArgIndex + 1) % 2] = mainArg;

        return args;
    }

}
