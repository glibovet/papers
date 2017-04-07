package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
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
        this.id = id;
        this.cssSelector = Preconditions.checkNotNull(cssSelector, "css selector expected");
    }

}
