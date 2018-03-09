package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.process.OnHandle;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executes handler's methods using specified by the method execution policy
 */
final class ProcessInvokeExecutor implements Invokeable {

    private static final Comparator<OnHandle.CallPolicy> EXECUTION_CMP = (o1, o2) -> {
        if (o1 != o2) {
            if (o1 == OnHandle.CallPolicy.BEFORE) return -1;
            if (o1 == OnHandle.CallPolicy.AFTER) return 1;
        }
        return 0;
    };

    private final Map<OnHandle.CallPolicy, List<ProcessInvoker>> policyToInvoker;

    ProcessInvokeExecutor(@NonNull Collection<? extends ProcessInvoker> invokers) {
        policyToInvoker = invokers.stream().collect(
                Collectors.groupingBy(i -> i.getOnHandle().policy(), () -> new TreeMap<>(EXECUTION_CMP), Collectors.toList())
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
