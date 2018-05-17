package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.var;
import lombok.extern.java.Log;
import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.annotation.Context;
import ua.com.papers.crawler.core.processor.annotation.util.InvokerUtil;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.v2.process.Binding;
import ua.com.papers.crawler.settings.v2.process.Handles;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@ToString
public final class MethodInvoker implements GroupedInvoker {

    private final Method method;
    private final Object target;

    private final MethodInfo methodInfo;
    private final List<ArgumentInfo> argumentsInfo;

    private final Context context;
    private final ArgsSupplierFactory supplierFactory;

    public MethodInvoker(@NonNull Method method, @NonNull Object target, @NonNull Context context) {
        this.argumentsInfo = Arrays.stream(method.getParameters()).map(p -> new ArgumentInfo(p, method))
                .collect(Collectors.toList());
        this.methodInfo = new MethodInfo(method.getAnnotation(Handles.class));
        this.method = method;
        this.target = target;
        this.context = context;
        this.supplierFactory = new ArgsSupplierFactory(context);
    }

    public static boolean canHandle(Method method) {
        return method.isAnnotationPresent(Handles.class) || hasBindingAnnotation(method);
    }

    @Override
    public int group() {
        return methodInfo.getHandles().map(Handles::group).orElse(Handles.DEFAULT);
    }

    @Override
    public Handles.CallPolicy executionPolicy() {
        return methodInfo.getHandles().map(Handles::policy).orElse(Handles.CallPolicy.INSIDE);
    }

    @Override
    public void invoke(Page page, PageSetting settings) {
        if (methodInfo.getHandles().isPresent()) {
            Arrays.stream(methodInfo.getHandles().get().selectors()).map(s -> page.toDocument().select(s))
                    .flatMap(Collection::stream)
                    .forEach(e -> invoke(page, e, settings));
        } else {
            invoke(page, page.toDocument().body(), settings);
        }
    }

    private void invoke(@NonNull Page page, @NonNull Element root, @NonNull PageSetting settings) {
        val suppliers = argumentsInfo.stream().map(info -> supplierFactory.newSupplier(info, page, root, settings))
                .collect(Collectors.toList());

        val invokeArgs = new Object[suppliers.size()];
        var runLoop = true;

        do {
            for (int i = 0, len = suppliers.size(); runLoop && i < len; ++i) {
                val supplier = suppliers.get(i);

                runLoop = supplier.suppliesOneArgument() || MethodInvoker.canProvideArguments(supplier);

                if (runLoop && supplier.hasMoreArguments()) {
                    invokeArgs[i] = supplier.nextArgument();
                }
            }

            if (runLoop) {
                InvokerUtil.invokeWrappingError(method, target, invokeArgs);
                Arrays.fill(invokeArgs, null);
            }

        } while (runLoop &= MethodInvoker.canProvideArguments(suppliers));

        logUnhandledSuppliers(page, suppliers);
    }

    private void logUnhandledSuppliers(Page page, Collection<? extends ArgsSupplier> suppliers) {
        val unhandledSuppliers = suppliers.stream()
                .filter(s -> !s.suppliesOneArgument() && s.hasMoreArguments())
                .collect(Collectors.toList());

        if (!unhandledSuppliers.isEmpty()) {

            val sb = new StringBuilder(String.format("Unhandled data detected for method %s, of %s on page %s,\n",
                    method, target, page.getUrl()));

            suppliers.forEach(s -> sb.append("Bindings=").append(s.getArgumentInfo().getBinding())
                    .append(", unprocessed?=").append(s.hasMoreArguments() && !s.suppliesOneArgument()));

            log.log(Level.WARNING, sb.toString());
        }
    }

    private static boolean canProvideArguments(ArgsSupplier supplier) {
        return !supplier.suppliesOneArgument() && (supplier.hasMoreArguments() || supplier.acceptsNull());
    }

    private static boolean canProvideArguments(Collection<? extends ArgsSupplier> suppliers) {
        return suppliers.stream().anyMatch(MethodInvoker::canProvideArguments);
    }

    private static boolean hasBindingAnnotation(Method method) {
        return Arrays.stream(method.getParameterAnnotations()).flatMap(Arrays::stream)
                .anyMatch(a -> a.annotationType().isAssignableFrom(Binding.class));
    }

}