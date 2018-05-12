package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.var;
import lombok.extern.java.Log;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.annotation.CallAdapter;
import ua.com.papers.crawler.core.processor.annotation.Context;
import ua.com.papers.crawler.core.processor.annotation.util.InvokerUtil;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.settings.v2.process.Binding;
import ua.com.papers.crawler.settings.v2.process.Converts;
import ua.com.papers.crawler.settings.v2.process.Handles;
import ua.com.papers.crawler.util.Preconditions;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public final class BindingInvoker implements GroupedInvoker {

    private static final Collection<Class<? extends Annotation>> NON_NULL_ANNOTATIONS = Arrays.asList(
            NotNull.class, Nonnull.class
    );

    private final Method method;
    private final Object target;

    private final MethodInfo methodInfo;
    private final List<ArgumentInfo> argumentsInfo;

    public BindingInvoker(@NonNull Method method, @NonNull Object target, @NonNull Context context) {
        this.argumentsInfo = Arrays.stream(method.getParameters()).map(p -> BindingInvoker.newArgumentInfo(p, method, context))
                .collect(Collectors.toList());
        this.methodInfo = new MethodInfo(method.getAnnotation(Handles.class));
        this.method = method;
        this.target = target;
    }

    public static boolean canHandle(Method method) {
        return method.isAnnotationPresent(Handles.class) || hasBindingAnnotation(method);
    }

    @Override
    public int group() {
        return methodInfo.getHandles() == null ? Handles.DEFAULT : methodInfo.getHandles().group();
    }

    @Override
    public Handles.CallPolicy executionPolicy() {
        return methodInfo.getHandles() == null ? Handles.CallPolicy.INSIDE : methodInfo.getHandles().policy();
    }

    @Override
    public void invoke(Page page) {
        val handles = methodInfo.getHandles();

        if (handles == null) {
            invoke(page, page.toDocument().body());
        } else {
            Arrays.stream(handles.selectors()).map(s -> page.toDocument().select(s)).flatMap(Collection::stream)
                    .forEach(e -> invoke(page, e));
        }
    }

    private void invoke(@NonNull Page page, @NonNull Element root) {
        val suppliers = argumentsInfo.stream().map(info -> new InvocationArgSupplier(info, page, root))
                .collect(Collectors.toList());

        val invokeArgs = new Object[suppliers.size()];
        var runLoop = true;

        do {
            for (int i = 0, len = suppliers.size(); runLoop && i < len; ++i) {
                val supplier = suppliers.get(i);

                runLoop = supplier.suppliesInfiniteArguments() || BindingInvoker.canProvideArguments(supplier);

                if (runLoop && supplier.hasMoreArguments()) {
                    invokeArgs[i] = supplier.nextArgument();
                }
            }

            if (runLoop) {
                InvokerUtil.invokeWrappingError(method, target, invokeArgs);
                Arrays.fill(invokeArgs, null);
            }

        } while (runLoop &= BindingInvoker.canProvideArguments(suppliers));

        checkUnhandledSuppliers(page, suppliers);
    }

    private void checkUnhandledSuppliers(Page page, Collection<InvocationArgSupplier> suppliers) {
        val unhandledSuppliers = suppliers.stream()
                .filter(s -> s.suppliesFiniteArguments() && s.hasMoreArguments())
                .collect(Collectors.toList());

        if (!unhandledSuppliers.isEmpty()) {

            val sb = new StringBuilder(String.format("Unhandled data detected for method %s, of %s on page %s,\n", method, target, page.getUrl()));

            suppliers.forEach(s -> sb.append("Bindings=").append(s.getMeta().getBinding())
                    .append(", unprocessed?=").append(s.hasMoreArguments() && s.suppliesFiniteArguments()));

            log.log(Level.WARNING, sb.toString());
        }
    }

    private static ArgumentInfo newArgumentInfo(Parameter parameter, Method method, Context context) {
        val bindingAnnotations = parameter.getAnnotationsByType(Binding.class);
        val acceptsNull = BindingInvoker.acceptsNull(parameter);
        val adapter = BindingInvoker.newCallAdapter(parameter, context);

        if (bindingAnnotations.length > 0) {

            BindingInvoker.checkBindingArgumentOrThrow(bindingAnnotations, method, parameter);

            val binding = bindingAnnotations[0];

            return new ArgumentInfo(acceptsNull, adapter, binding);
        }

        return new ArgumentInfo(acceptsNull, adapter);
    }

    @SuppressWarnings("unchecked")
    private static CallAdapter<?> newCallAdapter(Parameter parameter, Context context) {
        return BindingInvoker.extractConverter(parameter).map(c -> new CallAdapter(parameter.getType(), context, c)).orElseGet(() -> new CallAdapter(parameter.getType(), context));
    }

    private static Optional<Class<? extends Converter<?>>> extractConverter(Parameter parameter) {
        val converts = parameter.getAnnotation(Converts.class);

        return converts == null ? Optional.empty() : Optional.of(converts.converter());
    }

    private static boolean canProvideArguments(InvocationArgSupplier supplier) {
        return supplier.suppliesFiniteArguments() && (supplier.hasMoreArguments() || supplier.acceptsNull());
    }

    private static boolean canProvideArguments(Collection<InvocationArgSupplier> suppliers) {
        for (val supplier : suppliers) {
            if (canProvideArguments(supplier)) {
                return true;
            }
        }
        return false;
    }

    private static boolean acceptsNull(Parameter parameter) {
        return !parameter.getType().isAssignableFrom(Page.class) && NON_NULL_ANNOTATIONS.stream().noneMatch(a -> parameter.getAnnotation(a) != null);
    }

    private static void checkBindingArgumentOrThrow(Binding[] bindingAnnotations, Method method, Parameter param) {
        Preconditions.checkArgument(bindingAnnotations.length == 1,
                "Invalid %s annotation count, should be only one for method %s, parameter %s",
                Binding.class.getName(), method, param);

        Preconditions.checkArgument(!param.getType().isAssignableFrom(Page.class),
                "Argument which accepts %s shouldn't be annotated by %s. Method %s, parameter %s",
                Page.class, Binding.class.getName(), method, param);

        InvokerUtil.checkCssSelectorsThrowing(bindingAnnotations[0].selectors(), method, param);
    }

    private static boolean hasBindingAnnotation(Method method) {
        val arr = method.getParameterAnnotations();

        for (val row : arr) {
            for (val cell : row) {
                if (cell.annotationType().isAssignableFrom(Binding.class)) {
                    return true;
                }
            }
        }

        return false;
    }

}

@Value
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
final class InvocationArgSupplier {
    @Getter(value = AccessLevel.NONE)
    List<?> args;

    ArgumentInfo meta;
    Page page;

    InvocationArgSupplier(@NonNull ArgumentInfo meta, @NonNull Page page, @NonNull Element root) {
        this.meta = meta;
        this.page = page;

        if (meta.getBinding() == null) {
            this.args = InvocationArgSupplier.extractNodes(page, root, meta.getConverter());
        } else {
            this.args = InvocationArgSupplier.extractNodes(page, root, meta.getBinding().selectors(), meta.getConverter());
        }
    }

    boolean suppliesInfiniteArguments() {
        return meta.getBinding() == null;
    }

    boolean suppliesFiniteArguments() {
        return !suppliesInfiniteArguments();
    }

    boolean acceptsNull() {
        return meta.isNullable();
    }

    boolean hasMoreArguments() {
        return suppliesInfiniteArguments() || !args.isEmpty();
    }

    @NotNull
    Object nextArgument() {
        if (suppliesInfiniteArguments()) {
            return args.get(0);
        }

        Preconditions.checkArgument(!args.isEmpty(),
                "Trying to get next argument from empty arguments list %s", this);

        val next = args.get(0);

        args.remove(0);
        return next;
    }

    private static List<?> extractNodes(@NonNull Page page, @NonNull Element root, @NonNull String[] selectors, @NonNull Converter<?> converter) {
        return Arrays.stream(selectors).flatMap(css -> root.select(css).stream()).map(e -> converter.convert(e, page)).collect(Collectors.toCollection(LinkedList::new));
    }

    private static List<?> extractNodes(@NonNull Page page, @NonNull Element root, @NonNull Converter<?> converter) {
        return Collections.singletonList(converter.convert(root, page));
    }

}
