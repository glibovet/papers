package ua.com.papers.crawler.settings.v1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Method, marked with this annotation should be invoked before other methods
 * in the class, marked with {@linkplain PageHandler} annotation
 * </p>
 * Created by Максим on 11/27/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreHandle {

    int PAGE = 0;

    int group() default PAGE;
}
