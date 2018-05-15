package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.NonNull;

import javax.validation.constraints.NotNull;

interface ArgsSupplier {

    boolean suppliesOneArgument();

    boolean acceptsNull();

    boolean hasMoreArguments();

    @NonNull
    ArgumentInfo getArgumentInfo();

    @NotNull
    Object nextArgument();

}
