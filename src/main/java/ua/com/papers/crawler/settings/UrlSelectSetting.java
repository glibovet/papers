package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/18/2016.
 */
@Value
@Builder
public class UrlSelectSetting {

    public enum Action {SELECT, IGNORE}

    String cssSelector;
    String attrName;
    Action action;

    public UrlSelectSetting(@NotNull String cssSelector, @NotNull String attrName, @Nullable Action action) {
        this.cssSelector = Preconditions.checkNotNull(cssSelector);
        this.attrName = Preconditions.checkNotNull(attrName);
        this.action = action;
    }

    public UrlSelectSetting(@NotNull String cssSelector, @NotNull String attrName) {
        this(cssSelector, attrName, Action.SELECT);
    }
}
