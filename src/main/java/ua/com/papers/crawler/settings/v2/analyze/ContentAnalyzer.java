package ua.com.papers.crawler.settings.v2.analyze;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ContentAnalyzer {
    /**
     * page weight, should be equal or greater than zero
     */
    int weight() default 70;

    /**
     * Any valid css selector
     */
    @NotNull String selector();
}
