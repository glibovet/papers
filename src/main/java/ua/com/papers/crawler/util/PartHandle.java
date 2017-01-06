package ua.com.papers.crawler.util;

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
public @interface PartHandle {

    /**
     * page part id
     */
    int partId();

    /**
     * defines whether html tags should
     * be escaped and raw text returned
     * instead
     */
    boolean escapeHtml() default true;

}
