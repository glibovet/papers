package ua.com.papers.crawler.core.processor.annotation.process;

import ua.com.papers.crawler.core.processor.convert.SkipAdapter;
import ua.com.papers.crawler.core.processor.convert.IPartAdapter;

import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnHandle {
    int DEFAULT = 0;

    /**
     * Css selectors this method would like to handle. Shouldn't be null or empty.
     */
    @NotNull
    String[] selectors();

    /**
     * Represents handling group id. Methods, annotated with same group will be executed
     * separately from other groups. Order of the execution within one group and the same
     * call policy isn't specified
     */
    int group() default DEFAULT;

    /**
     * Call policy to apply. For example, if method uses {@linkplain CallPolicy#BEFORE}, then it will
     * be executed before other methods within same group
     */
    @NotNull
    CallPolicy policy() default CallPolicy.IN;

    /**
     * @return converter to apply when transforming page content part
     * into method argument. By default {@linkplain org.jsoup.nodes.Element} is returned
     */
    @NotNull
    Class<? extends IPartAdapter<?>> converter() default SkipAdapter.class;

    enum CallPolicy {
        BEFORE, IN, AFTER
    }
}
