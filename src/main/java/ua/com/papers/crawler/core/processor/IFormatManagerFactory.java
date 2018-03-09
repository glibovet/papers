package ua.com.papers.crawler.core.processor;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by Максим on 12/19/2016.
 */
@Validated
public interface IFormatManagerFactory {

    @NotNull
    IFormatManager create(@NotNull Collection<Object> handlers);

}
