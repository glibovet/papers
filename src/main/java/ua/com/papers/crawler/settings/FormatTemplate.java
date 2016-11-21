package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import ua.com.papers.crawler.util.IBuilder;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * Represents single &lt;format&gt; tag, which
 * is used to configure formatted output for page fragment
 * </p>
 * Created by Максим on 11/27/2016.
 */
public final class FormatTemplate implements Comparable<FormatTemplate> {

    private final String cssSelector;
    private final int id;
    private final int order;

    public static final class Builder implements IBuilder<FormatTemplate> {

        private final String cssSelector;
        private final int id;
        private int order;

        public Builder(@NotNull String cssSelector, int id) {
            this.id = id;
            this.cssSelector = Preconditions.checkNotNull(cssSelector, "css selector expected");
        }

        public Builder setOrder(int order) {

            if (order < 1)
                throw new IllegalArgumentException(
                        String.format("invalid order arg, was %d < 1", order));

            this.order = order;
            return this;
        }

        public String getCssSelector() {
            return cssSelector;
        }

        public int getId() {
            return id;
        }

        public int getOrder() {
            return order;
        }

        @Override
        public FormatTemplate build() {
            return new FormatTemplate(this);
        }

    }

    private FormatTemplate(Builder builder) {
        this.id = builder.getId();
        this.cssSelector = builder.getCssSelector();
        this.order = builder.getOrder();
    }

    public int getId() {
        return id;
    }

    public String getCssSelector() {
        return cssSelector;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(FormatTemplate o) {
        return Integer.compare(order, o.order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormatTemplate that = (FormatTemplate) o;

        if (id != that.id) return false;
        if (order != that.order) return false;
        return cssSelector.equals(that.cssSelector);

    }

    @Override
    public int hashCode() {
        int result = cssSelector.hashCode();
        result = 31 * result + id;
        result = 31 * result + order;
        return result;
    }

    @Override
    public String toString() {
        return "FormatTemplate{" +
                "cssSelector='" + cssSelector + '\'' +
                ", id=" + id +
                ", order=" + order +
                '}';
    }
}
