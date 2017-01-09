package ua.com.papers.crawler.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     Annotation to mark page handler
 * </p>
 * Created by Максим on 11/27/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageHandler {

    /**
     * Page id to handle
     */
    int id();

}
