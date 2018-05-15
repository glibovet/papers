package ua.com.papers.crawler.settings.v2.process;

import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

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

    enum CallPolicy {

        /**
         * Denotes this method should be invoked before other methods within same group
         */
        BEFORE(0),
        /**
         * Denotes this method should be invoked together with other methods within same group
         */
        INSIDE(1),
        /**
         * Denotes this method should be invoked after other methods within same group
         */
        AFTER(2);

        private final int position;

        CallPolicy(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public static final Comparator<CallPolicy> DEFAULT_COMPARATOR = Comparator.comparingInt(o -> o.position);
    }

    /*final class Stub implements Converter<Object> {
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
    }*/
}