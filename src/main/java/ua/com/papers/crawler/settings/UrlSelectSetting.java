package ua.com.papers.crawler.settings;

import lombok.Value;

import javax.validation.constraints.NotNull;

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

    public UrlSelectSetting(@NotNull String cssSelector, @NotNull String attrName) {
        Conditions.isNotNull(cssSelector);
        Conditions.isNotNull(attrName);
        Conditions.checkArgument(!cssSelector.isEmpty());
        Conditions.checkArgument(!attrName.isEmpty());
        this.cssSelector = cssSelector;
        this.attrName = attrName;
    }

}
