package ua.com.papers.crawler.core.processor.annotation.util;

import lombok.NonNull;
import lombok.val;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.main.vo.PageID;
import ua.com.papers.crawler.core.processor.exception.ProcessException;
import ua.com.papers.crawler.settings.v2.PageHandler;
import ua.com.papers.crawler.settings.v2.process.AfterPage;
import ua.com.papers.crawler.settings.v2.process.BeforePage;
import ua.com.papers.crawler.settings.v2.process.Handles;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public final class InvokerUtil {

    private InvokerUtil() {
        throw new RuntimeException();
    }

    @NonNull
    public static PageID newPageId(@NonNull PageHandler handler, @NonNull Object o) {
        if (TextUtils.isEmpty(handler.id())) {
            return new PageID(o.getClass().getName());
        }

        return new PageID(handler.id());
    }

    public static void invokeWrappingError(@NonNull Method m, @NonNull Object o, @NonNull Object... args) {
        try {
            m.invoke(o, args);
        } catch (final Throwable e) {
            throw new ProcessException(String.format("Couldn't invoke method %s, of object %s with args %s",
                    m, o, Arrays.toString(args)), e);
        }
    }

    public static void checkCssSelectorsThrowing(@NonNull String[] selectors, @NonNull Method method, @NonNull Parameter parameter) {
        for (val css : selectors) {
            Preconditions.checkArgument(TextUtils.isNonEmpty(css), "Invalid css selector, shouldn't be empty " +
                    "for method %s and parameter %s", method, parameter);
        }
    }

    public static void checkCssSelectorsThrowing(@NonNull String[] selectors) {
        for (val css : selectors) {
            Preconditions.checkArgument(TextUtils.isNonEmpty(css), "Invalid css selector found");
        }
    }

    /**
     * Checks whether given method is valid for later processing.
     * The method is considered to be a valid if it either annotated with
     * {@linkplain BeforePage} or {@linkplain AfterPage} and has <= 1 argument of type {@linkplain Page}
     * or it is annotated with {@linkplain Handles}, has 1 or 2 arguments, one of which is of type {@linkplain Page}
     */
    public static void checkMethodOrThrow(@NonNull Method method, @NonNull Object handler) {
        val argsLen = method.getParameterTypes().length;
        // annotated method doesn't have annotation at all or accepts one or zero arguments
        val preCond = InvokerUtil.checkLifecycleMethod(BeforePage.class, method);
        val postCond = InvokerUtil.checkLifecycleMethod(AfterPage.class, method);
        val partCond = method.getAnnotation(Handles.class) != null;

        Preconditions.checkArgument(!partCond || argsLen == 1 || argsLen == 2,
                String.format("Method annotated with %s can accept one or two arguments:" +
                        " void foo(t T, [arg1 %s])", Handles.class, Page.class.getName()));

        // annotations counter
        val count = Arrays.stream(new Boolean[]{preCond, postCond, partCond}).filter(b -> b).count();

        if (count > 1) {
            // only one annotation allowed per method!
            throw new IllegalStateException(
                    String.format("two or more annotations %s, %s, %s on method %s in class %s",
                            BeforePage.class, AfterPage.class, Handles.class, method, handler.getClass()));
        }
    }

    public static boolean checkLifecycleMethod(Class<? extends Annotation> a, Method m) {
        val present = m.isAnnotationPresent(a);

        if (present) {
            Preconditions.checkArgument(m.getParameterTypes().length <= 1, String.format(
                    "Method annotated with %s should either have zero or one argument of %s", a, Page.class));
        }
        return present;
    }

    /*public static Class<?> getRawType(Type type) {
        Preconditions.checkNotNull(type, "type == null");

        if (type instanceof Class<?>) {
            // Type is a normal class.
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) throw new IllegalArgumentException();
            return (Class<?>) rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }

        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }*/

}
