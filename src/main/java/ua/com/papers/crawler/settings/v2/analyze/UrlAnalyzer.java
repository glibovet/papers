package ua.com.papers.crawler.settings.v2.analyze;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UrlAnalyzer {

    String selector();

    String attribute();

    String baseUrl() default "";

}
