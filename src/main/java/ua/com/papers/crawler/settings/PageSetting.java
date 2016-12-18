package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import ua.com.papers.crawler.core.domain.vo.PageID;

import java.util.Collection;

/**
 * <p>
 * This class represents settings for a single web page
 * </p>
 * Created by Максим on 11/27/2016.
 */
@Value
@Builder(builderClassName = "Builder")
public final class PageSetting {

    public static final int MIN_WEIGHT = 0;
    public static final int MAX_WEIGHT = 100;
    public static final int DEFAULT_WEIGHT = 70;

    PageID id;
    int minWeight;
    @Singular
    Collection<? extends AnalyzeTemplate> analyzeTemplates;
    @Singular
    Collection<? extends FormatTemplate> formatTemplates;
    @Singular
    Collection<? extends UrlSelectSetting> selectSettings;

    private PageSetting(PageID id, int minWeight,
                        Collection<? extends AnalyzeTemplate> analyzeTemplates,
                        Collection<? extends FormatTemplate> formatTemplates,
                        Collection<? extends UrlSelectSetting> selectSettings) {

        if (minWeight < 0)
            throw new IllegalArgumentException(
                    String.format("min weight < 0, was %s", minWeight));

        if (Preconditions.checkNotNull(analyzeTemplates, "analyze templates == null")
                .isEmpty())
            throw new IllegalArgumentException("no analyze templates specified");

        this.id = Preconditions.checkNotNull(id);
        this.minWeight = minWeight;
        // since lombok builder provides us with unmodifiable collections
        // references just can be copied
        this.formatTemplates = Preconditions.checkNotNull(formatTemplates);// may be empty
        this.analyzeTemplates = analyzeTemplates;
        this.selectSettings = selectSettings;
    }
}
