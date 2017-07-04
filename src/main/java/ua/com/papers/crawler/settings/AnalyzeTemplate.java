package ua.com.papers.crawler.settings;

import lombok.Value;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * Represents single &lt;analyze&gt; tag, which
 * is used to configure single analyze template for part
 * of a page. </p>
 * <p>
 * <p>Every template has it's weight, css selector and,
 * optionally
 * </p>
 * Created by Максим on 11/27/2016.
 */
@Value
public class AnalyzeTemplate {

    String cssSelector;
    int weight;

    /**
     * @param cssSelector css selector to apply
     * @param weight      weight of css selector
     * @throws NullPointerException     if css selector equals null
     * @throws IllegalArgumentException if weight is less than zero
     */
    public AnalyzeTemplate(@NotNull String cssSelector, int weight) {
        Conditions.isNotNull(cssSelector, "css selector == null!");
        Conditions.checkArgument(!cssSelector.isEmpty(), "css selector expected");

        if (weight < 0)
            throw new IllegalArgumentException(
                    String.format("weight < 0, was %s", weight));

        this.cssSelector = cssSelector;
        this.weight = weight;
    }

}
