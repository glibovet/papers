package ua.com.papers.crawler.settings.v2.process;

import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.convert.Converter;

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

    /**
     * <p>
     * Converter type to apply when page content transformation into actual method argument is needed. Note that annotated method's
     * argument type should be assignable from converter's transformation return type. In this case converter will be
     * created via default constructor using reflection
     * </p>
     * <p>If {@linkplain Handles.Stub} is used, then acceptable converter will be searched among
     * registered adapters by the corresponding {@linkplain OutFormatter}.
     * If no acceptable converter is found - error is raised
     * </p>
     */
    @NotNull
    Class<? extends Converter<?>> converter() default Handles.Stub.class;

}
