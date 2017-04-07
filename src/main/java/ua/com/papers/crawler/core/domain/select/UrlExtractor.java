package ua.com.papers.crawler.core.domain.select;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.extern.java.Log;
import lombok.val;
import org.elasticsearch.common.collect.Tuple;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.settings.UrlSelectSetting;
import ua.com.papers.crawler.util.PageUtils;
import ua.com.papers.crawler.util.Url;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/18/2016.
 */
@Value
@Log
public class UrlExtractor implements IUrlExtractor {

    Map<PageID, Collection<? extends UrlSelectSetting>> idToSetting;

    public UrlExtractor(@NotNull Map<PageID, Collection<? extends UrlSelectSetting>> idToSetting) {

        if (Preconditions.checkNotNull(idToSetting, "id to settings == null").isEmpty())
            throw new IllegalArgumentException("url select settings wasn't specified!");

        this.idToSetting = Collections.unmodifiableMap(idToSetting);
    }

    @Override
    public Set<URL> extract(@NotNull(message = "Page id == null") PageID id,
                            @NotNull(message = "Cannot extract urls from null page") Page page) {

        val selectSettings = idToSetting.get(id);

        return selectSettings == null || !PageUtils.canParse(page.getContentType()) ? Collections.emptySet() : selectSettings
                .stream()
                // transform select setting into tuple which contains both selected elements and setting
                .map(setting -> new Tuple<>(page.toDocument().select(setting.getCssSelector()), setting))
                // get selected elements and transform them into urls
                .map(tuple -> tuple.v1()
                        .stream()
                        .map(elem -> elem.absUrl(tuple.v2().getAttrName()))
                        // since method #elem.absUrl returns non-empty strings
                        // for valid urls, we don't need to wrap url instance
                        // creation in try/catch block
                        .filter(absUrl -> absUrl.length() != 0)
                        .map(Url::new)
                        .map(Url::getUrl)
                        .collect(Collectors.toSet())
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
