package ua.com.papers.crawler.core.processor.util;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.settings.v1.PostHandle;
import ua.com.papers.crawler.settings.v1.PreHandle;
import ua.com.papers.crawler.util.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

public final class ProcessorUtil {

    private ProcessorUtil() {
        throw new IllegalStateException();
    }

    public static boolean isPreHandler(@NonNull Method m) {
        val a = m.getAnnotation(PreHandle.class);
        return a != null && a.group() == PreHandle.PAGE;
    }

    public static boolean isPostHandler(@NonNull Method m) {
        val a = m.getAnnotation(PostHandle.class);
        return a != null && a.group() == PostHandle.PAGE;
    }

    @SneakyThrows
    public static void invokeLifecycleMethod(Method m, Page page, Object who) {
        val args = m.getParameterTypes();

        if (args.length == 1 && args[0].isAssignableFrom(Page.class)) {
            m.invoke(who, page);
        } else {
            m.invoke(who);
        }
    }

    @SneakyThrows
    public static void invokeProcessMethod(Method m, Object who, Object arg) {
        m.invoke(who, arg);
    }

    public static int plusOne(boolean bool) {
        return bool ? 1 : 0;
    }

    public static boolean checkLifecycleMethod(Class<? extends Annotation> a, Method m) {
        val present = m.isAnnotationPresent(a);

        if (present) {
            Preconditions.checkArgument(m.getParameterTypes().length <= 1, String.format(
                    "Method annotated with %s should either have zero or one argument of %s", a, Page.class));
        }
        return present;
    }

}
