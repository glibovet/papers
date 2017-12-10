package ua.com.papers.crawler.settings;

import lombok.Value;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Optional;

/**
 * <p>
 *     Describes rule to select link from page
 * </p>
 * Created by Максим on 12/18/2016.
 */
@Value
public class UrlSelectSetting {

    String cssSelector;
    String attrName;
    URL baseUrl;

    public UrlSelectSetting(@NotNull String cssSelector, @NotNull String attrName) {
        this(cssSelector, attrName, null);
    }

    public UrlSelectSetting(@NotNull String cssSelector, @NotNull String attrName, @Nullable URL baseUrl) {
        Conditions.isNotNull(cssSelector);
        Conditions.isNotNull(attrName);
        Conditions.checkArgument(!cssSelector.isEmpty());
        Conditions.checkArgument(!attrName.isEmpty());
        this.cssSelector = cssSelector;
        this.attrName = attrName;
        this.baseUrl = baseUrl;
    }

    public Optional<URL> getBaseUrl() {
        return Optional.ofNullable(baseUrl);
    }

}
