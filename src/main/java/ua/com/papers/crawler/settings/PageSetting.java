package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import ua.com.papers.crawler.util.IBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Максим on 11/27/2016.
 */
public final class PageSetting {

    public static final int MIN_WEIGHT = 0;
    public static final int MAX_WEIGHT = 100;
    public static final int DEFAULT_WEIGHT = 70;

    private final int minWeight;
    private final Collection<AnalyzeTemplate> analyzeTemplates;
    private final Collection<FormatTemplate> formatTemplates;

    public static final class Builder implements IBuilder<PageSetting> {

        private final int minWeight;
        private final Collection<AnalyzeTemplate> analyzeTemplates;
        private final Collection<FormatTemplate> formatTemplates;

        public Builder(int minWeight) {

            if (minWeight > PageSetting.MAX_WEIGHT)
                throw new IllegalArgumentException(
                        String.format("illegal min weight, was %d > %d", minWeight, PageSetting.MAX_WEIGHT));

            this.minWeight = minWeight;
            this.analyzeTemplates = new ArrayList<>();
            this.formatTemplates = new ArrayList<>();
        }

        public int getMinWeight() {
            return minWeight;
        }

        public Builder addAnalyzeTemplate(@NotNull AnalyzeTemplate template) {
            analyzeTemplates.add(Preconditions.checkNotNull(template));
            return this;
        }

        public Builder addAnalyzeTemplate(@NotNull Collection<AnalyzeTemplate> templates) {
            analyzeTemplates.addAll(Preconditions.checkNotNull(templates));
            return this;
        }

        public Collection<AnalyzeTemplate> getAnalyzeTemplates() {
            return Collections.unmodifiableCollection(analyzeTemplates);
        }

        public Builder addFormatTemplate(@NotNull FormatTemplate template) {
            formatTemplates.add(Preconditions.checkNotNull(template));
            return this;
        }

        public Builder addFormatTemplate(@NotNull Collection<FormatTemplate> templates) {
            formatTemplates.addAll(Preconditions.checkNotNull(templates));
            return this;
        }

        public Collection<FormatTemplate> getFormatTemplates() {
            return Collections.unmodifiableCollection(formatTemplates);
        }

        @Override
        public PageSetting build() {
            return new PageSetting(this);
        }
    }

    private PageSetting(Builder builder) {
        this.minWeight = builder.getMinWeight();
        this.analyzeTemplates = Collections.unmodifiableCollection(builder.getAnalyzeTemplates());
        this.formatTemplates = Collections.unmodifiableCollection(builder.getFormatTemplates());
    }

    public int getMinWeight() {
        return minWeight;
    }

    public Collection<AnalyzeTemplate> getAnalyzeTemplates() {
        return analyzeTemplates;
    }

    public Collection<FormatTemplate> getFormatTemplates() {
        return formatTemplates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageSetting that = (PageSetting) o;

        if (minWeight != that.minWeight) return false;
        if (!analyzeTemplates.equals(that.analyzeTemplates)) return false;
        return formatTemplates.equals(that.formatTemplates);

    }

    @Override
    public int hashCode() {
        int result = minWeight;
        result = 31 * result + analyzeTemplates.hashCode();
        result = 31 * result + formatTemplates.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PageSetting{" +
                "minWeight=" + minWeight +
                ", analyzeTemplates=" + analyzeTemplates +
                ", formatTemplates=" + formatTemplates +
                '}';
    }
}
