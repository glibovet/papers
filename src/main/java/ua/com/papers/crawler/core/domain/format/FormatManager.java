package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.convert.IPartAdapter;
import ua.com.papers.crawler.core.domain.format.convert.StringAdapter;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.util.*;

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
    Object lock = new Object();
    /**
     * Map of cached converters
     */
    private static final Map<Class<? extends IPartAdapter<?>>, IPartAdapter<?>> CACHE;

    private interface Invoker {

        void invoke();

    }

    @Value
    private static class LifecycleInvoker implements Invoker {

        Page page;
        List<Tuple<Method, Object>> target;

        LifecycleInvoker(Page page, Method m, Object who) {
            this.page = page;
            this.target = new ArrayList<>(1);
            addMethod(m, who);
        }

        void addMethod(Method m, Object who) {
            target.add(new Tuple<>(m, who));
        }

        @Override
        public void invoke() {
            target.forEach(m -> FormatManager.invokeLifecycleMethod(m.getV1(), page, m.getV2()));
        }

    }

    @Value
    private static class HandlerInvoker implements Invoker {

        Page page;
        int group;
        List<Tuple<Method, Object>> preHandlers;
        List<Tuple<Method, Object>> handlers;
        List<Tuple<Method, Object>> postHandlers;
        List<RawContent> content;

        HandlerInvoker(int group, Page page, List<RawContent> contents) {
            this.group = group;
            this.page = page;
            this.content = Collections.unmodifiableList(contents);
            this.preHandlers = new ArrayList<>(1);
            this.postHandlers = new ArrayList<>(1);
            this.handlers = new ArrayList<>();
        }

        void addHandler(Method m, Object who) {
            val actual = m.getAnnotation(Handler.class).group();
            Preconditions.checkArgument(actual == group,
                    String.format("Illegal group, was %d, should be %d", actual, group));
            handlers.add(new Tuple<>(m, who));
        }

        void addPreHandler(Method m, Object who) {
            val actual = m.getAnnotation(PreHandle.class).group();
            Preconditions.checkArgument(actual == group,
                    String.format("Illegal group, was %d, should be %d", actual, group));
            preHandlers.add(new Tuple<>(m, who));
        }

        void addPostHandler(Method m, Object who) {
            val actual = m.getAnnotation(PostHandle.class).group();
            Preconditions.checkArgument(actual == group,
                    String.format("Illegal group, was %d, should be %d", actual, group));
            postHandlers.add(new Tuple<>(m, who));
        }

        @Override
        public void invoke() {
            // group
            content.forEach(raw -> {
                // pre group
                preHandlers.forEach(m -> FormatManager.invokeLifecycleMethod(m.getV1(), page, m.getV2()));

                val idToPart = raw.getIdToPart();

                handlers.forEach(m -> {

                    val annotation = m.getV1().getAnnotation(Handler.class);

                    if (idToPart.containsKey(annotation.id())) {

                        val elem = idToPart.get(annotation.id());
                        val converter = FormatManager.getConverter(annotation.converter());

                        FormatManager.invokeProcessMethod(m.getV1(), m.getV2(), converter.convert(elem));
                    }
                });
                // post group
                postHandlers.forEach(m -> FormatManager.invokeLifecycleMethod(m.getV1(), page, m.getV2()));
            });
        }
    }

    static {
        CACHE = Collections.synchronizedMap(
                new HashMap<Class<? extends IPartAdapter<?>>, IPartAdapter<?>>() {
                    {
                        // register default converters
                        put(Handler.SkipAdapter.class, Handler.SkipAdapter.instance);
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

        if (handlers != null && !handlers.isEmpty()) {

            val content = pageFormatter.format(pageID, page);
            val preInvokers = new ArrayList<LifecycleInvoker>();
            val invokers = new ArrayList<HandlerInvoker>();
            val postInvokers = new ArrayList<LifecycleInvoker>();

            for (val handler : handlers) {
                preInvokers.addAll(extractLifecyclePreInvokers(page, handler));
                invokers.addAll(extractHandlerInvokers(page, handler, content));
                postInvokers.addAll(extractLifecyclePostInvokers(page, handler));
            }

            synchronized (lock) {
                preInvokers.forEach(LifecycleInvoker::invoke);
                invokers.forEach(HandlerInvoker::invoke);
                postInvokers.forEach(LifecycleInvoker::invoke);
            }
        }
    }

    private List<LifecycleInvoker> extractLifecyclePreInvokers(Page page, Object h) {
        val result = new ArrayList<LifecycleInvoker>(1);

        for (val m : h.getClass().getMethods()) {

            FormatManager.checkMethod(m, h);
            // global lifecycle method
            val preCond = m.isAnnotationPresent(PreHandle.class)
                    && m.getAnnotation(PreHandle.class).group() == PreHandle.PAGE;

            if (preCond) {
                result.add(new LifecycleInvoker(page, m, h));
            }
        }

        return result;
    }

    private List<LifecycleInvoker> extractLifecyclePostInvokers(Page page, Object h) {
        val result = new ArrayList<LifecycleInvoker>(1);

        for (val m : h.getClass().getMethods()) {

            FormatManager.checkMethod(m, h);
            // global lifecycle method
            val postCond = m.isAnnotationPresent(PostHandle.class)
                    && m.getAnnotation(PostHandle.class).group() == PostHandle.PAGE;

            if (postCond) {
                result.add(new LifecycleInvoker(page, m, h));
            }
        }

        return result;
    }

    private static List<HandlerInvoker> extractHandlerInvokers(Page page, Object h, List<RawContent> contents) {
        val result = new ArrayList<HandlerInvoker>();

        for (val m : h.getClass().getMethods()) {

            FormatManager.checkMethod(m, h);
            // annotated method doesn't have annotation at all or accepts one or zero arguments
            val pre = m.getAnnotation(PreHandle.class);
            val post = m.getAnnotation(PostHandle.class);
            val part = m.getAnnotation(Handler.class);

            if (pre == null && post == null && part == null) continue;

            // current method is annotated with @PostHandle, @PreHandle or @Handler
            val group = part != null ? m.getAnnotation(Handler.class).group()
                    : (post != null ? m.getAnnotation(PostHandle.class).group()
                    : m.getAnnotation(PreHandle.class).group());

            if ((group == Handler.PAGE && part != null) || group != Handler.PAGE) {

                // such methods were already registered
                // for later invocation
                boolean found = false;

                for (val invoker : result) {

                    if (invoker.group == group) {
                        addHandlerForAnnotation(invoker, m, h);
                        found = true;
                    }
                }

                if (!found) {
                    val invoker = new HandlerInvoker(group, page, contents);
                    addHandlerForAnnotation(invoker, m, h);
                    result.add(invoker);
                }
            }
        }
        return result;
    }

    private static void addHandlerForAnnotation(HandlerInvoker invoker, Method m, Object h) {

        val pre = m.getAnnotation(PreHandle.class);
        val post = m.getAnnotation(PostHandle.class);

        if (pre != null) {
            invoker.addPreHandler(m, h);
        } else if (post != null) {
            invoker.addPostHandler(m, h);
        } else {
            invoker.addHandler(m, h);
        }
    }

    private static void checkMethod(Method method, Object handler) {
        val argsLen = method.getParameterTypes().length;
        // annotated method doesn't have annotation at all or accepts one or zero arguments
        val preCond = FormatManager.checkLifecycleMethod(PreHandle.class, method);
        val postCond = FormatManager.checkLifecycleMethod(PostHandle.class, method);
        val partCond = method.getAnnotation(Handler.class) != null;

        Preconditions.checkArgument(!partCond || argsLen == 1, String.format(
                "Method annotated with %s can accept exactly one argument (see converter generic param)", Handler.class));
        // annotations counter
        val cnt = plusOne(preCond) + plusOne(postCond) + plusOne(partCond);

        if (cnt > 1)
            // only one annotation allowed per method!
            throw new IllegalStateException(
                    String.format("two or more annotations %s, %s, %s on method %s in class %s",
                            PreHandle.class, PostHandle.class, Handler.class, method, handler.getClass()));
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
        return Preconditions.checkNotNull(result, String.format("No handler for %s", cl));
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
