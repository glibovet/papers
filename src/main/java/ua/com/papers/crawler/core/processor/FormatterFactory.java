package ua.com.papers.crawler.core.processor;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by Максим on 12/19/2016.
 */
@Validated
public interface FormatterFactory {

    @NotNull
    OutFormatter create(@NotNull Collection<?> handlers);

}
