package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.extern.java.Log;
import lombok.val;
import org.jsoup.select.Elements;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.convert.IPartAdapter;
import ua.com.papers.crawler.core.domain.format.convert.StringAdapter;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.Handler;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/19/2016.
 */
@Value
@Log
@Getter(value = AccessLevel.NONE)
public class FormatManager implements IFormatManager {

    private static final int DEFAULT_LIFECYCLE_METHODS_CNT = 2;

    IPageFormatter pageFormatter;
    Map<PageID, ? extends Collection<Object>> idToHandlers;
    /**
     * Map of cached converters
     */
    private static final Map<Class<? extends IPartAdapter<?>>, IPartAdapter<?>> CACHE;

    static {
        CACHE = Collections.synchronizedMap(
                new HashMap<Class<? extends IPartAdapter<?>>, IPartAdapter<?>>() {
                    {
                        // register default converters
                        put(Handler.DummyAdapter.class, Handler.DummyAdapter.instance);
                        put(StringAdapter.class, StringAdapter.instance);
                    }
                }
        );
    }

    public static Collection<IPartAdapter<?>> getRegisteredConverters() {
        return Collections.unmodifiableCollection(CACHE.values());
    }

    @SuppressWarnings("unchecked")
    public static void registerConverter(@NotNull IPartAdapter<?> converter) {
        Preconditions.checkNotNull(converter);
        CACHE.put((Class<? extends IPartAdapter<?>>) converter.getClass(), converter);
    }

    public FormatManager(@NotNull Collection<Object> handlers, IPageFormatter pageFormatter) {

        this.pageFormatter = Preconditions.checkNotNull(pageFormatter);
        FormatManager.checkHandlers(handlers);

        this.idToHandlers = Preconditions.checkNotNull(handlers)
                .stream()
                .collect(Collectors.groupingBy(h -> new PageID(h.getClass().getAnnotation(PageHandler.class).id()),
                        Collectors.mapping(h -> h, Collectors.toList()))
                );
    }

    @Override
    public void processPage(@NotNull PageID pageID, @NotNull Page page) {
        val handlers = idToHandlers.get(pageID);

        if (handlers != null) {

            val idToPart = pageFormatter.format(pageID, page)
                    .getIdToPart()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));

            handlers.forEach(h -> invokeHandleMethods(page, idToPart, h));
        }
    }

    private void invokeHandleMethods(Page page, Map<Integer, Elements> idToPart, Object handler) {

        val cpyIdToPart = new HashMap<>(idToPart);
        val preHandlers = new HashSet<Method>(DEFAULT_LIFECYCLE_METHODS_CNT);
        val postHandlers = new HashSet<Method>(DEFAULT_LIFECYCLE_METHODS_CNT);
        val partHandlers = new HashSet<Method>();

        for (val method : handler.getClass().getMethods()) {

            val argsLen = method.getParameterTypes().length;
            // annotated method doesn't have annotation at all or accepts one or zero arguments
            val preCond = FormatManager.checkLifecycleMethod(PreHandle.class, method);
            val postCond = FormatManager.checkLifecycleMethod(PostHandle.class, method);
            // annotated method handles page part
            val partAnnotation = method.getAnnotation(Handler.class);

            Preconditions.checkArgument(partAnnotation == null || argsLen == 1, String.format(
                    "Method annotated with %s can accept exactly one argument (see converter generic param)", Handler.class));

            val partCond = partAnnotation != null && idToPart.containsKey(partAnnotation.id());
            // annotations counter
            val cnt = plusOne(preCond) + plusOne(postCond) + plusOne(partCond);

            if (cnt > 1)
                // only one annotation allowed per method!
                throw new IllegalStateException(
                        String.format("two or more annotations %s, %s, %s on method %s in class %s",
                                PreHandle.class, PostHandle.class, Handler.class, method, handler.getClass()));

            if (cnt > 0) {

                if (partCond)
                    partHandlers.add(method);
                else if (postCond)
                    postHandlers.add(method);
                else
                    preHandlers.add(method);
            }
        }

        preHandlers.forEach(m -> FormatManager.invokeLifecycleMethod(m, page, handler));
        partHandlers.forEach(m -> {
            val annotation = m.getAnnotation(Handler.class);
            val elem = idToPart.get(annotation.id());
            val converter = FormatManager.getConverter(annotation.converter());

            elem.forEach(e -> FormatManager.invokeProcessMethod(m, handler, converter.convert(e)));
            cpyIdToPart.remove(annotation.id());
        });
        postHandlers.forEach(m -> FormatManager.invokeLifecycleMethod(m, page, handler));

        if (!cpyIdToPart.isEmpty()) {
            log.log(Level.WARNING, String.format("No handler methods found for map (id => content):\n%s", cpyIdToPart));
        }
    }

    private static boolean checkLifecycleMethod(Class<? extends Annotation> a, Method m) {
        val present = m.isAnnotationPresent(a);

        if (present) {
            Preconditions.checkArgument(m.getParameterTypes().length <= 1, String.format(
                    "Method annotated with %s should either have zero or one argument of %s", a, Page.class));
        }
        return present;
    }

    private static IPartAdapter<?> getConverter(Class<? extends IPartAdapter<?>> cl) {

        IPartAdapter<?> result = CACHE.get(cl);

        if (result == null) {
            // such converter wasn't found in cache
            // try load converter through reflection
            try {
                result = cl.getConstructor().newInstance();
                CACHE.put(cl, result);
            } catch (final NoSuchMethodException e) {
                throw new RuntimeException(String.format("%s should define non-arg constructor", cl), e);
            } catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(String.format("An error occurred while creating a new instance of %s", cl), e);
            }
        }
        return result;
    }

    private static void invokeLifecycleMethod(Method m, Page page, Object who) {

        val args = m.getParameterTypes();

        try {

            if (args.length == 1 && args[0].isAssignableFrom(Page.class)) {
                m.invoke(who, page);
            } else {
                m.invoke(who);
            }
        } catch (final Exception e) {
            log.log(Level.SEVERE, String.format("Failed to invoke method %s for class %s", m, who.getClass()), e);
            // finish with error!
            throw new RuntimeException(e);
        }
    }

    private static void invokeProcessMethod(Method m, Object who, Object arg) {

        try {
            m.invoke(who, arg);
        } catch (final Exception e) {
            log.log(Level.SEVERE, String.format("Failed to invoke method %s for class %s", m, who.getClass()), e);
            // finish with error!
            throw new RuntimeException(e);
        }
    }

    private static int plusOne(boolean bool) {
        return bool ? 1 : 0;
    }

    private static void checkHandlers(Collection<Object> handlers) {

        if (Preconditions.checkNotNull(handlers, "handlers == null").isEmpty())
            throw new IllegalArgumentException("handlers weren't specified");

        for (final Object handler : handlers)
            if (!handler.getClass().isAnnotationPresent(PageHandler.class))
                throw new IllegalArgumentException(
                        String.format("%s class must be annotated with %s", handler.getClass(), PageHandler.class));
    }

}
