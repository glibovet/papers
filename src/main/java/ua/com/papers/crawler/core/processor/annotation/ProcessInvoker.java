package ua.com.papers.crawler.core.processor.annotation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.var;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.process.OnHandle;
import ua.com.papers.crawler.core.processor.annotation.util.AnnotationUtil;
import ua.com.papers.crawler.core.processor.convert.IPartAdapter;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
public final class ProcessInvoker implements Invokeable {

    private final Method method;
    private final Object target;
    private final OnHandle onHandle;
    private final IPartAdapter<?> converter;
    private final int auxiliaryArgIndex;

    ProcessInvoker(@NonNull Method method, @NonNull Object target, @NonNull Function<Class<?>, IPartAdapter<?>> supplier) {
        AnnotationUtil.checkMethodOrThrow(method, target);

        val process = Preconditions.checkNotNull(method.getAnnotation(OnHandle.class),
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

            auxiliaryArgIndex = isPageAssignable ? 1 : 0;
            transformArg = params[auxiliaryArgIndex + 1 % 2];
        }

        val adapter = Preconditions.checkNotNull(supplier.apply(transformArg),
                String.format("Wasn't found adapter for class %s", transformArg));

        this.method = method;
        this.target = target;
        this.onHandle = process;
        this.converter = adapter;
        this.auxiliaryArgIndex = auxiliaryArgIndex;
    }

    @NonNull
    public OnHandle getOnHandle() {
        return onHandle;
    }

    @Override
    public void invoke(@NonNull Page page) throws InvocationTargetException, IllegalAccessException {
        for (val css : onHandle.selectors()) {

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
        args[auxiliaryArgIndex + 1 % 2] = mainArg;

        return args;
    }

}
