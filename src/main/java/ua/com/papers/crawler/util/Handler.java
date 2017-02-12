package ua.com.papers.crawler.util;

import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.format.convert.IPartAdapter;

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
public @interface Handler {
    /**
     * Just returns element as it was passed without
     * modifications
     */
    class DummyAdapter implements IPartAdapter<Element> {

        public static final DummyAdapter instance = new DummyAdapter();

        private DummyAdapter() {
        }

        @Override
        public Element convert(@NotNull Element element) {
            return element;
        }
    }

    /**
     * @return page part id
     */
    int id();

    int group() default 0;

    /**
     * @return converter to apply when transforming page content part
     * into method argument. By default page part will be returned
     */
    @NotNull
    Class<? extends IPartAdapter<?>> converter() default DummyAdapter.class;

}
