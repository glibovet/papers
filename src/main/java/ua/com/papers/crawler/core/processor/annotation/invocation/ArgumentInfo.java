package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.jodah.typetools.TypeResolver;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.annotation.util.InvokerUtil;
import ua.com.papers.crawler.core.processor.convert.ElementConverter;
import ua.com.papers.crawler.settings.v2.process.Binding;
import ua.com.papers.crawler.settings.v2.process.Converts;
import ua.com.papers.crawler.util.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class ArgumentInfo {

    private static final Collection<Class<? extends Annotation>> NON_NULL_ANNOTATIONS = Arrays.asList(
            NotNull.class, Nonnull.class
    );

    boolean isNullable;

    Parameter parameter;
    Method method;

    Class<?> primaryType;
    @Nullable
    @Getter(value = AccessLevel.NONE)
    Class<?> primaryTypeArg;

    @Nullable
    @Getter(value = AccessLevel.NONE)
    Binding binding;

    ArgumentInfo(@NonNull Parameter parameter, @NonNull Method method) {
        this.isNullable = ArgumentInfo.acceptsNull(parameter);
        this.binding = ArgumentInfo.extractBinding(parameter).orElse(null);
        this.parameter = parameter;
        this.method = method;

        primaryType = parameter.getType();

        Class<?> genericTypeArg = null;

        if (parameter.getParameterizedType() instanceof ParameterizedType) {
            Preconditions.checkArgument(Collection.class.isAssignableFrom(primaryType), "Unknown parameter type, was %s", primaryType);

            val paramType = parameter.getParameterizedType();
            val genericParams = TypeResolver.resolveRawArguments(paramType, paramType.getClass());

            Preconditions.checkArgument(genericParams.length == 1,
                    "Can't handle generic parameter %s. Generic parameter should contain only one generic argument", parameter);

            genericTypeArg = genericParams[0];

            Preconditions.checkArgument(!TypeResolver.Unknown.class.isAssignableFrom(genericTypeArg),
                    "Wildcards aren't allowed for a collection argument type, parameter=%s in method=%s", parameter, method);
        }

        primaryTypeArg = genericTypeArg;
    }

    @NotNull
    public Optional<Binding> getBinding() {
        return Optional.ofNullable(binding);
    }

    @NotNull
    public Optional<Class<?>> getPrimaryTypeArg() {
        return Optional.ofNullable(primaryTypeArg);
    }

    private static Optional<Binding> extractBinding(@NonNull Parameter parameter) {
        val bindingAnnotations = parameter.getAnnotationsByType(Binding.class);

        if (bindingAnnotations.length > 0) {

            ArgumentInfo.checkBindingArgumentOrThrow(bindingAnnotations, parameter);

            return Optional.of(bindingAnnotations[0]);
        }

        return Optional.empty();
    }

    private static void checkBindingArgumentOrThrow(Binding[] bindingAnnotations, Parameter param) {
        Preconditions.checkArgument(bindingAnnotations.length == 1,
                "Invalid %s annotation count, should be only one, parameter %s",
                Binding.class.getName(), param);

        Preconditions.checkArgument(!param.getType().isAssignableFrom(Page.class),
                "Argument which accepts %s shouldn't be annotated by %s. Parameter %s",
                Page.class, Binding.class.getName(), param);

        InvokerUtil.checkCssSelectorsThrowing(bindingAnnotations[0].selectors());
    }

    private static boolean acceptsNull(Parameter parameter) {
        return !parameter.getType().isAssignableFrom(Page.class) && NON_NULL_ANNOTATIONS.stream().noneMatch(a -> parameter.getAnnotation(a) != null);
    }

    private static Optional<Class<? extends ElementConverter<?>>> extractConverter(Parameter parameter) {
        val converts = parameter.getAnnotation(Converts.class);

        return converts == null ? Optional.empty() : Optional.of(converts.converter());
    }

}
