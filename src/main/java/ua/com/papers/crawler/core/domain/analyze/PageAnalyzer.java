package ua.com.papers.crawler.core.domain.analyze;

import com.google.common.base.Preconditions;
import lombok.Value;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Максим on 12/1/2016.
 */
@Value
public class PageAnalyzer implements IPageAnalyzer {

    PageID pageID;
    Collection<? extends IAnalyzeChain> chains;
    int minSum;

    public PageAnalyzer(int minSum, @NotNull PageID pageID, @NotNull Collection<? extends IAnalyzeChain> chains) {

        if (minSum < 0)
            throw new IllegalArgumentException(
                    String.format("min sum < 0, was %d", minSum));

        this.minSum = minSum;
        this.pageID = Preconditions.checkNotNull(pageID);
        this.chains = Collections.unmodifiableCollection(Preconditions.checkNotNull(chains));
    }

    @Override
    public Result analyze(@NotNull Page page) {

        int weightSum = 0;

        for (final IAnalyzeChain chain : chains) {
            weightSum += chain.analyze(page.getDocument());
        }
        return new Result(pageID, weightSum);
    }
}
