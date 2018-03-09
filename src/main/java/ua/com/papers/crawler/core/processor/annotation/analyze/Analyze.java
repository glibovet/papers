package ua.com.papers.crawler.core.processor.annotation.analyze;

import ua.com.papers.crawler.settings.PageSetting;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Analyze {
    /**
     * page weight, should be equal or greater than zero
     */
    int weight() default PageSetting.DEFAULT_WEIGHT;

    /**
     * Any valid css selector
     */
    @NotNull String selector();
}
