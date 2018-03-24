package ua.com.papers.crawler.core.processor.annotation;

import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.IFormatManagerFactory;

import java.util.Collection;

public final class AnnotationFormatManagerFactory implements IFormatManagerFactory {
    @Override
    public OutFormatter create(Collection<?> handlers) {
        return new AnnotationFormatManager(handlers);
    }
}
