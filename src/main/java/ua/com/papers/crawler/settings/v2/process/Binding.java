package ua.com.papers.crawler.settings.v2.process;

import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Binding {
    /**
     * CSS selectors for a page content this method would like to handle. Shouldn't be null or empty.
     */
    @NotNull
    String[] selectors();

}
