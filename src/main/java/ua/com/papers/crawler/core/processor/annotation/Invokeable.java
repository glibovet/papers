package ua.com.papers.crawler.core.processor.annotation;

import lombok.NonNull;
import ua.com.papers.crawler.core.domain.bo.Page;

import java.lang.reflect.InvocationTargetException;

interface Invokeable {
    void invoke(@NonNull Page page) throws InvocationTargetException, IllegalAccessException;
}
