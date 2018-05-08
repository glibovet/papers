package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.experimental.var;
import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Executes handler's methods using specified by the method execution policy.
 * All invokers are assumed to be of the same invocation group
 */
final class SingleGroupInvoker implements Invoker {

    private static final Comparator<ProcessInvoker> EXECUTOR_CMP = (o1, o2) -> Handles.CallPolicy.DEFAULT_COMPARATOR.compare(o1.getHandles().policy(), o2.getHandles().policy());

    private static final BinaryOperator<List<? extends Element>> EXCEPTION_ON_DUPLICATE_MERGER = (e1, e2) -> {
        throw new IllegalStateException(String.format("Found duplicate for %s, %s", e1, e2));
    };

    private final Collection<? extends ProcessInvoker> invokers;

    SingleGroupInvoker(@NonNull Collection<? extends ProcessInvoker> invokers) {
        this.invokers = invokers.stream().sorted(EXECUTOR_CMP).collect(Collectors.toList());
    }

    @Override
    public void invoke(Page page) throws InvocationTargetException, IllegalAccessException {
        val invokerToArgs = invokers.stream().collect(Collectors.toMap(e -> e, v -> v.extractNodes(page), EXCEPTION_ON_DUPLICATE_MERGER, () -> new LinkedHashMap<>(invokers.size())));
        var elementsLeft = invokerToArgs.entrySet().stream().mapToInt(e -> e.getValue().size()).sum();

        while (elementsLeft > 0) {
            for (val entry : invokerToArgs.entrySet()) {
                val it = entry.getValue().iterator();

                if (it.hasNext()) {
                    entry.getKey().invoke(page, it.next());
                    it.remove();
                    --elementsLeft;
                }
            }
        }
    }
}
