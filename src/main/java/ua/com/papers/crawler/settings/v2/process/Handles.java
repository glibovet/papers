package ua.com.papers.crawler.settings.v2.process;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.convert.Converter;

import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handles {
    int DEFAULT = 0;

    /**
     * CSS selectors for a page content this method would like to handle. Shouldn't be null or empty.
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
    CallPolicy policy() default CallPolicy.INSIDE;

    /**
     * <p>
     * Converter type to apply when page content transformation into actual method argument is needed. Note that annotated method's
     * argument type should be assignable from converter's transformation return type. In this case converter will be
     * created via default constructor using reflection
     * </p>
     * <p>If {@linkplain Stub} is used, then acceptable converter will be searched among
     * registered adapters by the corresponding {@linkplain OutFormatter}.
     * If no acceptable converter is found - error is raised
     * </p>
     */
    @NotNull
    Class<? extends Converter<?>> converter() default Stub.class;

    enum CallPolicy {
        /**
         * Denotes this method should be invoked before other methods within same group
         */
        BEFORE,
        /**
         * Denotes this method should be invoked together with other methods within same group
         */
        INSIDE,
        /**
         * Denotes this method should be invoked after other methods within same group
         */
        AFTER
    }

    final class Stub implements Converter<Object> {
        private Stub() {
            throw new IllegalStateException("Shouldn't be instantiated");
        }

        @Override
        public Class<?> converts() {
            throw new IllegalStateException("Shouldn't be invoked");
        }

        @Override
        public Object convert(Element element, Page page) {
            throw new IllegalStateException("Shouldn't be invoked");
        }
    }
}