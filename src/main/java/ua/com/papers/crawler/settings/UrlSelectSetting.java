package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/18/2016.
 */
@Value
@Builder
public class UrlSelectSetting {

    String cssSelector;
    String attrName;

    public UrlSelectSetting(@NotNull String cssSelector, @NotNull String attrName) {
        this.cssSelector = Preconditions.checkNotNull(cssSelector);
        this.attrName = Preconditions.checkNotNull(attrName);
    }

}
