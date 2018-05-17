package ua.com.papers.crawler.core.processor.convert;

import lombok.NonNull;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.OutFormatter;
import ua.com.papers.crawler.settings.PageSetting;

/**
 * <p>
 * Class which transforms input {@linkplain I} into desired data type {@linkplain R}
 * </p>
 * <p>
 * In order to be created via reflection a no-args constructor should be supplied, in another case this adapter should
 * be registered via {@linkplain OutFormatter#registerAdapter(Converter)}
 * </p>
 */
public interface Converter<I, R> {

    /**
     * @return data class that will be returned as a result of
     * mapping
     */
    @NonNull
    Class<? extends R> converts();

    /**
     * Converts {@linkplain I} into {@linkplain R}
     *
     * @param i        element to convert
     * @param page     page to format
     * @param settings page settings
     * @return transformed instance of {@linkplain I}
     */
    @NonNull
    R convert(@NonNull I i, @NonNull Page page, @NonNull PageSetting settings);

}
