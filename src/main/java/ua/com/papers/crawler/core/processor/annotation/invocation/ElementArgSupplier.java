package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ua.com.papers.crawler.util.Preconditions;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class ElementArgSupplier implements ArgsSupplier {

    @Getter
    ArgumentInfo meta;

    Collection<?> args;

    ElementArgSupplier(@NonNull ArgumentInfo meta, @NonNull List<?> args) {
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
        return suppliesOneArgument() || !args.isEmpty();
    }

    @Override
    public ArgumentInfo getArgumentInfo() {
        return meta;
    }

    @NotNull
    @Override
    public Object nextArgument() {
        if (suppliesOneArgument()) {
            return args.iterator().next();
        }

        Preconditions.checkArgument(!args.isEmpty(),
                "Trying to get next argument from empty arguments list %s", this);

        val it = args.iterator();
        val next = it.next();

        it.remove();
        return next;
    }

}
