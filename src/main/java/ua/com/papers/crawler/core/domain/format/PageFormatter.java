package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.core.domain.vo.PartID;
import ua.com.papers.crawler.settings.FormatTemplate;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/17/2016.
 */
@Value
public class PageFormatter implements IPageFormatter {

    Collection<? extends FormatTemplate> formatTemplates;

    public PageFormatter(@NotNull Collection<? extends FormatTemplate> formatTemplates) {
        this.formatTemplates = Collections.unmodifiableCollection(Preconditions.checkNotNull(formatTemplates));
    }

    @Override
    public FormattedPage format(@NotNull PageID id, @NotNull Page page) {

        val idToPart = formatTemplates
                .stream()
                .collect(Collectors
                        .toMap(t -> new PartID(t.getId()),
                                t -> page.toDocument().select(t.getCssSelector())
                        )
                );
        return new FormattedPage(id, idToPart);
    }
}
