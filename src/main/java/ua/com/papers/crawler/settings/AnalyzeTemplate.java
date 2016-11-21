package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import ua.com.papers.crawler.util.IBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 * Represents single &lt;analyze&gt; tag, which
 * is used to configure single analyze template for part
 * of a page. </p>
 * <p>
 * <p>Every template has it's weight, css selector and,
 * optionally, action which describes what should be done if page
 * fragment matches current template. e.g., if action is {@linkplain Action#REDIRECT},
 * then crawler will try to visit given page
 * </p>
 * Created by Максим on 11/27/2016.
 */
public final class AnalyzeTemplate {

    public enum Action {
        REDIRECT
    }

    private final Collection<Action> actions;
    private final String cssSelector;
    private final int weight;

    public static final class Builder implements IBuilder<AnalyzeTemplate> {

        private final String cssSelector;
        private final int weight;
        private final Collection<Action> actions;

        public Builder(@NotNull String cssSelector, int weight) {

            if (weight < 0)
                throw new IllegalArgumentException(
                        String.format("invalid weight arg, was %d < 0", weight));

            this.cssSelector = Preconditions.checkNotNull(cssSelector, "css selector expected");
            this.weight = weight;
            this.actions = new ArrayList<>(0);
        }

        public Builder addAction(@NotNull Action action) {
            actions.add(Preconditions.checkNotNull(action));
            return this;
        }

        public Builder addAction(@NotNull Collection<Action> actions) {
            actions.addAll(Preconditions.checkNotNull(actions));
            return this;
        }

        public Collection<Action> getActions() {
            return Collections.unmodifiableCollection(actions);
        }

        public String getCssSelector() {
            return cssSelector;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public AnalyzeTemplate build() {
            return new AnalyzeTemplate(this);
        }

    }

    private AnalyzeTemplate(Builder builder) {
        this.actions = Collections.unmodifiableCollection(builder.getActions());
        this.cssSelector = builder.getCssSelector();
        this.weight = builder.getWeight();
    }

    public Collection<Action> getActions() {
        return actions;
    }

    public String getCssSelector() {
        return cssSelector;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalyzeTemplate that = (AnalyzeTemplate) o;

        if (weight != that.weight) return false;
        if (!actions.equals(that.actions)) return false;
        return cssSelector.equals(that.cssSelector);

    }

    @Override
    public int hashCode() {
        int result = actions.hashCode();
        result = 31 * result + cssSelector.hashCode();
        result = 31 * result + weight;
        return result;
    }

    @Override
    public String toString() {
        return "AnalyzeTemplate{" +
                "actions=" + actions +
                ", cssSelector='" + cssSelector + '\'' +
                ", weight=" + weight +
                '}';
    }
}
