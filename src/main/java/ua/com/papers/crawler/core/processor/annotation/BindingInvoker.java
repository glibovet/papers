package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.var;
import lombok.val;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.settings.v2.process.Binding;
import ua.com.papers.crawler.util.Preconditions;
import ua.com.papers.crawler.util.TextUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

final class BindingInvoker implements Invoker {

    private static final Collection<Class<? extends Annotation>> NON_NULL_ANNOTATIONS = Arrays.asList(
            NotNull.class, Nonnull.class
    );

    private final Method method;
    private final Object target;

    @Value
    private static final class MethodMeta {
        private static final MethodMeta PAGE_PARAMETER = new MethodMeta();

        boolean isNullable;
        @Nullable Binding binding;
        Converter<?> converter;

        MethodMeta(boolean isNullable, Converter<?> converter, @Nullable Binding binding) {
            this.isNullable = isNullable;
            this.binding = binding;
            this.converter = converter;
        }

        private MethodMeta() {
            this(false, StubCallAdapter.getInstance(), null);
        }
    }

    private final List<MethodMeta> adapters;

    public BindingInvoker(@NonNull Method method, @NonNull Object target, @NonNull Context context) {
        val params = method.getParameters();

        this.adapters = new ArrayList<>(params.length);

        for (val param : params) {
            val paramClass = param.getType();
            val bindingAnnotations = param.getAnnotationsByType(Binding.class);

            if (paramClass.isAssignableFrom(Page.class)) {

                Preconditions.checkArgument(bindingAnnotations.length == 0,
                        "Argument which accepts %s shouldn't be annotated by %s. Method %s, parameter %s",
                        Page.class, Binding.class.getName(), method, param);

                adapters.add(MethodMeta.PAGE_PARAMETER);

            } else {

                Preconditions.checkArgument(bindingAnnotations.length == 1,
                        "Invalid %s annotation count, should be only one for method %s, parameter %s",
                        Binding.class.getName(), method, param);

                val binding = bindingAnnotations[0];

                BindingInvoker.checkCssSelectorsThrowing(binding.selectors(), method, param);

                adapters.add(new MethodMeta(BindingInvoker.acceptsNull(param), new CallAdapter(binding.converter(), paramClass, context), binding));
            }
        }

        this.method = method;
        this.target = target;
    }

    @Override
    public void invoke(Page page) throws InvocationTargetException, IllegalAccessException {

        @Value
        class InvocationArgs {
            MethodMeta meta;
            List<?> preparedArgs;
            boolean isPageArg;

            private InvocationArgs(MethodMeta meta) {
                this.meta = meta;
                this.isPageArg = meta == MethodMeta.PAGE_PARAMETER || meta.binding == null;

                if (isPageArg) {
                    preparedArgs = Collections.emptyList();
                } else  {
                    preparedArgs = BindingInvoker.extractNodes(page, meta.binding.selectors(), meta.converter);
                }
            }
        }

        val invokeArgs = adapters.stream().map(InvocationArgs::new).collect(Collectors.toList());
        val methodArgs = new Object[invokeArgs.size()];

        var elementsLeft = invokeArgs.stream().mapToInt(e -> e.isPageArg ? 0 : e.preparedArgs.size()).sum();
        var finished = elementsLeft == 0;

        while (!finished) {
            for (int i = 0, len = invokeArgs.size(); i < len; ++i) {

                val invokeArg = invokeArgs.get(i);

                if (invokeArg.isPageArg) {
                    methodArgs[i] = page;

                } else {

                    val argsIt = invokeArg.preparedArgs.iterator();

                    finished = (!argsIt.hasNext() && !invokeArg.meta.isNullable) || elementsLeft <= 0;

                    if (argsIt.hasNext()) {
                        methodArgs[i] = argsIt.next();
                        argsIt.remove();
                        --elementsLeft;
                    }
                }
            }

            invoke(methodArgs);
            Arrays.fill(methodArgs, null);
        }
    }

    private void invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
        method.invoke(target, args);
    }

    private static List<?> extractNodes(Page page, String[] selectors, Converter<?> converter) {
        return Arrays.stream(selectors).flatMap(css -> page.toDocument().select(css).stream()).map(e -> converter.convert(e, page)).collect(Collectors.toList());
    }

    private static void checkCssSelectorsThrowing(String[] selectors, Method method, Parameter parameter) {
        for (val css : selectors) {
            Preconditions.checkArgument(TextUtils.isNonEmpty(css), "Invalid css selector, shouldn't be empty " +
                    "for method %s and parameter %s", method, parameter);
        }
    }

    private static boolean acceptsNull(Parameter parameter) {
        return NON_NULL_ANNOTATIONS.stream().noneMatch(a -> parameter.getAnnotation(a) != null);
    }

}
