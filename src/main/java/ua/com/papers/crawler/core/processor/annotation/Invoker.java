package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import ua.com.papers.crawler.core.domain.bo.Page;

import java.lang.reflect.InvocationTargetException;

/**
 * Subclasses which implement this interface should invoke corresponding underlying handler's
 * method and pass given page as argument
 */
interface Invoker {
    /**
     * Implement to pass given page to an underlying handler
     *
     * @param page page to pass
     */
    void invoke(@NonNull Page page) throws InvocationTargetException, IllegalAccessException;
}
