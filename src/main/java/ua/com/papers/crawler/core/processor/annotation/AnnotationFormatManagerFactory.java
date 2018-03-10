package ua.com.papers.crawler.core.processor.annotation;

import ua.com.papers.crawler.core.processor.IFormatManager;
import ua.com.papers.crawler.core.processor.IFormatManagerFactory;

import java.util.Collection;

public final class AnnotationFormatManagerFactory implements IFormatManagerFactory {
    @Override
    public IFormatManager create(Collection<Object> handlers) {
        return new AnnotationFormatManager(handlers);
    }
}
