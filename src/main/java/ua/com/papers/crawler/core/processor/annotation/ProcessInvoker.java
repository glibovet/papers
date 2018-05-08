package ua.com.papers.crawler.core.processor.annotation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.var;
import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.util.AnnotationUtil;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.settings.v2.process.Handles;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents invoker for a method, annotated with {@linkplain Handles}.
 * It transforms supplied page into acceptable method arguments and invokes this method
 */
@EqualsAndHashCode
@ToString
public final class ProcessInvoker {

    private final Method method;
    private final Object target;
    private final Handles handles;
    private final Converter<?> converter;
    private final int auxiliaryArgIndex;

    <T> ProcessInvoker(@NonNull Method method, @NonNull Object target,
                       @NonNull Context context) {

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
            converter = context.getRawTypeConverter((Class<T>) transformArg);
        } else {
            // explicit adapter was supplied, use it
            converter = context.getAdapter((Class<? extends Converter<T>>) process.converter());
        }

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

    @NonNull
    public List<? extends Element> extractNodes(@NonNull Page page) {
        return Arrays.stream(handles.selectors()).flatMap(css -> page.toDocument().select(css).stream()).collect(Collectors.toList());
    }

    public void invoke(@NonNull Page page, @NonNull Element element) throws InvocationTargetException, IllegalAccessException {
        method.invoke(target, wrapArgs(converter.convert(element, page), page));
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
