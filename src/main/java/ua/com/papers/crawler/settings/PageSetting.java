package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import lombok.*;
import ua.com.papers.crawler.core.main.model.PageID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;

/**
 * This class represents settings for a single web page that should be processed by crawler
 */
@Value
@Builder
public final class PageSetting {

    PageID id;
    AnalyzeWeight minWeight;
    @Getter(value = AccessLevel.NONE) @Nullable URL baseUrl;

    @Singular
    Collection<? extends AnalyzeTemplate> analyzeTemplates;
    @Singular
    Collection<? extends FormatTemplate> formatTemplates;
    @Singular
    Collection<? extends UrlSelectSetting> selectSettings;

    private PageSetting(PageID id, AnalyzeWeight minWeight,
                        @Nullable URL baseUrl,
                        Collection<? extends AnalyzeTemplate> analyzeTemplates,
                        Collection<? extends FormatTemplate> formatTemplates,
                        Collection<? extends UrlSelectSetting> selectSettings) {

        Preconditions.checkArgument(!Preconditions.checkNotNull(analyzeTemplates, "analyze templates == null")
                .isEmpty(), "no analyze templates specified");

        this.id = Preconditions.checkNotNull(id);
        this.minWeight = minWeight;
        // since lombok builder provides us with unmodifiable collections
        // references just can be copied
        this.formatTemplates = Preconditions.checkNotNull(formatTemplates);// may be empty
        this.analyzeTemplates = analyzeTemplates;
        this.selectSettings = selectSettings;
        this.baseUrl = baseUrl;
    }

    @NotNull
    public Optional<URL> getBaseUrl() {
        return Optional.ofNullable(baseUrl);
    }

}
