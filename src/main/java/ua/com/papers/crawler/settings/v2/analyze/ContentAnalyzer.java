package ua.com.papers.crawler.settings.v2.analyze;

import ua.com.papers.crawler.settings.AnalyzeWeight;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ContentAnalyzer {
    /**
     * page weight, should be equal or greater than zero
     */
    int weight() default AnalyzeWeight.DEFAULT_WEIGHT;

    /**
     * Any valid css selector
     */
    @NotNull String selector();
}
