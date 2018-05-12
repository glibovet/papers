package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.NonNull;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Executes handler's methods using specified by the method execution policy.
 * All invokers are assumed to be of the same invocation group
 */
public final class SingleGroupInvoker implements Invoker {

    private static final Comparator<GroupedInvoker> EXECUTOR_CMP = (o1, o2) -> Handles.CallPolicy.DEFAULT_COMPARATOR.compare(o1.executionPolicy(), o2.executionPolicy());

    private final Collection<? extends GroupedInvoker> invokers;

    SingleGroupInvoker(@NonNull Collection<? extends GroupedInvoker> invokers) {
        this.invokers = invokers.stream().sorted(EXECUTOR_CMP).collect(Collectors.toList());
    }

    @Override
    public void invoke(Page page) {
        invokers.forEach(i -> i.invoke(page));
    }
}
