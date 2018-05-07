package ua.com.papers.crawler.core.processor.annotation;

import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.core.processor.FormatterFactory;

import java.util.Collection;

public final class AnnotationFormatterFactoryImp implements FormatterFactory {
    @Override
    public OutFormatter create(Collection<?> handlers) {
        return new AnnotationOutFormatterImp(handlers);
    }
}
