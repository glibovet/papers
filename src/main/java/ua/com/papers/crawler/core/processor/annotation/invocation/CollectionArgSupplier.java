package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ua.com.papers.crawler.util.Preconditions;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class CollectionArgSupplier implements ArgsSupplier {

    @Getter
    ArgumentInfo meta;

    Collection<?> args;
    @NonFinal
    boolean toggled;

    CollectionArgSupplier(@NonNull ArgumentInfo meta, @NonNull Collection<?> args) {
        this.meta = meta;
        this.args = args;
    }

    @Override
    public boolean suppliesOneArgument() {
        return !meta.getBinding().isPresent();
    }

    @Override
    public boolean acceptsNull() {
        return meta.isNullable();
    }

    @Override
    public boolean hasMoreArguments() {
        return !toggled;
    }

    @Override
    public ArgumentInfo getArgumentInfo() {
        return meta;
    }

    @NotNull
    @Override
    public Object nextArgument() {
        Preconditions.checkArgument(!toggled);
        toggled = true;
        return args;
    }

}
