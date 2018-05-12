package ua.com.papers.crawler.settings.v2.process;

import ua.com.papers.crawler.core.processor.convert.Converter;

import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate method or its arguments to provide a custom converter
 * class.
 *
 * @see Converter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.PARAMETER})
public @interface Converts {
    /**
     * Converter type to apply when page content transformation into actual method argument is needed. Note that annotated method's
     * argument type should be assignable from converter's transformation return type
     */
    @NotNull
    Class<? extends Converter<?>> converter();
}
