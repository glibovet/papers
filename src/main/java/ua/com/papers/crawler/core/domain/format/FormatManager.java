package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.extern.java.Log;
import lombok.val;
import org.jsoup.select.Elements;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PartHandle;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/19/2016.
 */
// todo processing methods caching
    // todo warning for non existing part methods
@Value
@Log
public class FormatManager implements IFormatManager {

    IPageFormatter pageFormatter;
    Map<PageID, ? extends Collection<Object>> idToHandlers;

    public FormatManager(@NotNull Collection<Object> handlers, IPageFormatter pageFormatter) {

        this.pageFormatter = Preconditions.checkNotNull(pageFormatter);
        FormatManager.checkHandlers(handlers);

        this.idToHandlers = Preconditions.checkNotNull(handlers)
                .stream()
                .collect(Collectors.groupingBy(h -> new PageID(h.getClass().getAnnotation(PageHandler.class).pageId()),
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

            handlers.forEach(h -> invokeHandleMethods(idToPart, h));
        }
    }

    private static void invokeHandleMethods(Map<Integer, Elements> idToPart, Object handler) {

        val preHandlers = new HashSet<Method>(2);
        val postHandlers = new HashSet<Method>(2);
        val partHandlers = new HashSet<Method>();

        for (val method : handler.getClass().getMethods()) {

            val args = method.getParameterTypes();
            val preCond = method.isAnnotationPresent(PreHandle.class) && args.length == 0;
            val postCond = method.isAnnotationPresent(PostHandle.class) && args.length == 0;
            val partAnnotation = method.getAnnotation(PartHandle.class);

            val partCond = partAnnotation != null && idToPart.containsKey(partAnnotation.partId())
                    && args.length == 1 && args[0].isAssignableFrom(String.class);
            // annotations counter
            val cnt = plusOne(preCond) + plusOne(postCond) + plusOne(partCond);

            if (cnt > 1)
                // only one annotation allowed per method!
                throw new IllegalStateException(
                        String.format("two or more annotations %s, %s, %s on method %s in class %s",
                                PreHandle.class, PostHandle.class, PartHandle.class, method, handler.getClass()));

            if (cnt > 0) {

                if (partCond)
                    partHandlers.add(method);
                else if (postCond)
                    postHandlers.add(method);
                else
                    preHandlers.add(method);
            }
        }

        preHandlers.forEach(m -> FormatManager.invokeLifecycleMethod(m, handler));
        partHandlers.forEach(m -> {
            val annotation = m.getAnnotation(PartHandle.class);
            val elem = idToPart.get(annotation.partId());

            if (annotation.escapeHtml()) {
                elem.forEach(e -> invokeProcessMethod(m, handler, e.text()));
            } else {
                elem.forEach(e -> invokeProcessMethod(m, handler, e.outerHtml()));
            }
        });
        postHandlers.forEach(m -> FormatManager.invokeLifecycleMethod(m, handler));
    }

    private static void invokeLifecycleMethod(Method m, Object which) {

        try {
            m.invoke(which);
        } catch (final Exception e) {
            log.log(Level.SEVERE, String.format("Failed to invoke method %s for class %s", m, which.getClass()), e);
            // finish with error!
            throw new RuntimeException(e);
        }
    }

    private static void invokeProcessMethod(Method m, Object which, String arg) {

        try {
            m.invoke(which, arg);
        } catch (final Exception e) {
            log.log(Level.SEVERE, String.format("Failed to invoke method %s for class %s", m, which.getClass()), e);
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
