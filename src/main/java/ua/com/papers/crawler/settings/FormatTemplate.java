package ua.com.papers.crawler.settings;

import lombok.Value;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * Represents single &lt;format&gt; tag, which
 * is used to configure formatted output for page fragment
 * </p>
 * Created by Максим on 11/27/2016.
 */
@Value
public class FormatTemplate {

    String cssSelector;
    int id;

    public FormatTemplate(int id, @NotNull String cssSelector) {
        Conditions.isNotNull(cssSelector, "css selector expected");
        Conditions.checkArgument(!cssSelector.isEmpty(), "css selector expected");
        this.id = id;
        this.cssSelector = cssSelector;
    }

}
