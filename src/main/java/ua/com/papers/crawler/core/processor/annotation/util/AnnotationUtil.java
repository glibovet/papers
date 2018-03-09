package ua.com.papers.crawler.core.processor.annotation.util;

import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.process.OnHandle;
import ua.com.papers.crawler.core.processor.util.ProcessorUtil;
import ua.com.papers.crawler.core.processor.xml.annotation.PostHandle;
import ua.com.papers.crawler.core.processor.xml.annotation.PreHandle;
import ua.com.papers.crawler.util.Preconditions;

import java.lang.reflect.*;
import java.util.Arrays;

public final class AnnotationUtil {

    private AnnotationUtil() {
        throw new RuntimeException();
    }

    public static void checkMethodOrThrow(Method method, Object handler) {
        val argsLen = method.getParameterTypes().length;
        // annotated method doesn't have annotation at all or accepts one or zero arguments
        val preCond = ProcessorUtil.checkLifecycleMethod(PreHandle.class, method);
        val postCond = ProcessorUtil.checkLifecycleMethod(PostHandle.class, method);
        val partCond = method.getAnnotation(OnHandle.class) != null;

        Preconditions.checkArgument(!partCond || argsLen == 1 || argsLen == 2,
                String.format("Method annotated with %s can accept one or two arguments:" +
                        " void foo(t T, [arg1 %s])", OnHandle.class, Page.class.getName()));

        // annotations counter
        val count = Arrays.stream(new Boolean[]{preCond, postCond, partCond}).map(b -> b ? 1 : 0).count();

        if (count > 1) {
            // only one annotation allowed per method!
            throw new IllegalStateException(
                    String.format("two or more annotations %s, %s, %s on method %s in class %s",
                            PreHandle.class, PostHandle.class, OnHandle.class, method, handler.getClass()));
        }
    }

    public static Class<?> getRawType(Type type) {
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
    }

}
