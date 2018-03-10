package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executes handler's methods using specified by the method execution policy
 */
final class ProcessInvokeExecutor implements Invoker {

    private static final Comparator<Handles.CallPolicy> EXECUTION_CMP = (o1, o2) -> {
        if (o1 != o2) {
            if (o1 == Handles.CallPolicy.BEFORE) return -1;
            if (o1 == Handles.CallPolicy.AFTER) return 1;
        }
        return 0;
    };

    private final Map<Handles.CallPolicy, ? extends List<? extends ProcessInvoker>> policyToInvoker;

    ProcessInvokeExecutor(@NonNull Collection<? extends ProcessInvoker> invokers) {
        policyToInvoker = invokers.stream().collect(
                Collectors.groupingBy(i -> i.getHandles().policy(), () -> new TreeMap<>(EXECUTION_CMP), Collectors.toList())
        );
    }

    @Override
    public void invoke(Page page) throws InvocationTargetException, IllegalAccessException {
        for (val entry : policyToInvoker.entrySet()) {
            for (val invoker : entry.getValue()) {
                invoker.invoke(page);
            }
        }
    }
}
