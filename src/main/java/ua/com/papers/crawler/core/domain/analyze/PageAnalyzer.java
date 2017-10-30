package ua.com.papers.crawler.core.domain.analyze;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.experimental.var;
import lombok.val;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.settings.AnalyzeTemplate;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 * Default implementation of {@linkplain IPageAnalyzer}
 * </p>
 * Created by Максим on 12/1/2016.
 */
@Value
public class PageAnalyzer implements IPageAnalyzer {

    PageID pageID;
    Collection<? extends AnalyzeTemplate> templates;
    int minSum;

    public PageAnalyzer(int minSum, @NotNull PageID pageID, @NotNull Collection<? extends AnalyzeTemplate> templates) {

        if (minSum < 0)
            throw new IllegalArgumentException(
                    String.format("min sum < 0, was %d", minSum));

        this.minSum = minSum;
        this.pageID = Preconditions.checkNotNull(pageID);
        this.templates = Collections.unmodifiableCollection(Preconditions.checkNotNull(templates));
    }

    @Override
    public Result analyze(@NotNull Page page) {

        val doc = page.toDocument();
        var weightSum = 0;

        for (val template : templates) {
            weightSum += doc.select(template.getCssSelector()).size() * template.getWeight();
        }
        return new Result(pageID, weightSum);
    }
}
