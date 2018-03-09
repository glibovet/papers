package ua.com.papers.crawler.core.processor.annotation.analyze;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used to mark a class which should handle crawler's output for a single page. This annotation
 * contain analyzing and processing directives to be used by crawler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Handler {

    /**
     * Should return unique id, so that
     * given handler can be distinguished among
     * other
     */
    int id();

    /**
     * Analyze rules to use
     */
    @NotNull Analyze[] rules();

    /**
     * Base url for links in case their full
     * url cannot be extracted from context
     */
    @Nullable String baseUrl() default "";

}
