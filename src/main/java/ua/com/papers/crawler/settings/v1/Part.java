package ua.com.papers.crawler.settings.v1;

import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.core.processor.convert.general.StubAdapter;

import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation to mark methods which are responsible for page parts
 * handling
 * </p>
 * Created by Максим on 11/27/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Part {

    int PAGE = 0;

    /**
     * @return page content part id
     */
    int id();

    /**
     * @return content group id
     */
    int group() default PAGE;

    /**
     * @return converter to apply when transforming page content part
     * into method argument. By default page content part will be returned
     */
    @NotNull
    Class<? extends Converter<?, ?>> converter() default StubAdapter.class;

}
