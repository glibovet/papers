package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.annotation.util.InvokerUtil;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.v2.process.BeforePage;
import ua.com.papers.crawler.util.Preconditions;

import java.lang.reflect.Method;

@EqualsAndHashCode
@ToString
public final class PreLifecycleInvoker implements Invoker {
    private final Method method;
    private final Object target;
    private boolean isPageRequired;

    public static boolean canHandle(Method method) {
        return method.isAnnotationPresent(BeforePage.class);
    }

    public PreLifecycleInvoker(@NonNull Method method, @NonNull Object target) {
        InvokerUtil.checkMethodOrThrow(method, target);
        Preconditions.checkArgument(PreLifecycleInvoker.canHandle(method), "Missing lifecycle annotation");

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
    public void invoke(Page page, PageSetting settings) {
        if (isPageRequired) {
            InvokerUtil.invokeWrappingError(method, target, page);
        } else {
            InvokerUtil.invokeWrappingError(method, target);
        }
    }
}
