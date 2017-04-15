package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.Value;
import ua.com.papers.crawler.settings.FormatTemplate;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Максим on 12/19/2016.
 */
@Value
public class FormatManagerFactory implements IFormatManagerFactory {

    Collection<? extends FormatTemplate> templates;

    public FormatManagerFactory(@NotNull Collection<? extends FormatTemplate> templates) {
        this.templates = Collections.unmodifiableCollection(Preconditions.checkNotNull(templates));
    }

    @Override
    @NotNull
    public IFormatManager create(@NotNull Collection<Object> handlers) {
        return new FormatManager(handlers, new PageFormatter(templates));
    }
}
