package ua.com.papers.crawler.core.domain.format;

import lombok.Value;
import ua.com.papers.crawler.settings.FormatTemplate;
import ua.com.papers.crawler.settings.PageSetting;
import ua.com.papers.crawler.settings.Settings;
import ua.com.papers.crawler.util.Preconditions;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Максим on 12/19/2016.
 */
@Value
public class FormatManagerFactory implements IFormatManagerFactory {

    Collection<? extends FormatTemplate> templates;
    Settings settings;

    public FormatManagerFactory(Settings settings) {
        this.settings = Preconditions.checkNotNull(settings);
        this.templates = Collections.unmodifiableCollection(settings.getPageSettings()
                .stream()
                .flatMap(
                        (Function<PageSetting, Stream<? extends FormatTemplate>>) setting -> setting.getFormatTemplates().stream()
                ).collect(Collectors.toList()));
    }

    @Override
    @NotNull
    public IFormatManager create(@NotNull Collection<Object> handlers) {
        return new FormatManager(handlers, new PageFormatter(templates));
    }
}
