package ua.com.papers.crawler.core.domain;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Максим on 12/1/2016.
 */
public final class PageAnalyzer implements IPageAnalyzer {

    private final int minAccept;
    private final Map<Integer, Collection<? extends IAnalyzeChain>> idToChains;
    // todo redo?
    public PageAnalyzer(int minAccept, @NotNull Map<Integer, Collection<? extends IAnalyzeChain>> chains) {
        this.minAccept = minAccept;
        this.idToChains = Collections.unmodifiableMap(chains);
    }

    @Override
    public boolean matches(@NotNull Page page) {

        int sumWeight = 0;

        for (final Collection<? extends IAnalyzeChain> chains : idToChains.values())
            for (final IAnalyzeChain chain : chains) {
                if (chain.satisfies(page)) {
                    sumWeight += chain.getWeight();
                }
            }

        return sumWeight >= minAccept;
    }
}
