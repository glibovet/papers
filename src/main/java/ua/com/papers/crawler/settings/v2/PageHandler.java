package ua.com.papers.crawler.settings.v2;

import ua.com.papers.crawler.settings.v2.analyze.ContentAnalyzer;
import ua.com.papers.crawler.settings.v2.analyze.UrlAnalyzer;

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
public @interface PageHandler {

    /**
     * Should return unique id, so that
     * given handler can be distinguished among
     * other
     */
    int id();

    int minWeight() default 70;

    /**
     * Analyze rules to use
     */
    @NotNull ContentAnalyzer[] analyzers();

    /**
     * Urls selectors to use
     */
    @NotNull UrlAnalyzer[] urlSelectors() default {};

    /**
     * Base url for links in case their full
     * url cannot be extracted from context
     */
    @Nullable String baseUrl() default "";

}
