package ua.com.papers.crawler.core.domain.analyze;

import com.google.common.base.Preconditions;
import lombok.Value;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Максим on 12/18/2016.
 */
@Value
public class AnalyzeManager implements IAnalyzeManager {

    Collection<? extends IPageAnalyzer> analyzers;

    public AnalyzeManager(@NotNull Collection<? extends IPageAnalyzer> analyzers) {
        this.analyzers = Collections.unmodifiableCollection(Preconditions.checkNotNull(analyzers));
    }

    @Override
    public boolean matches(@NotNull Page page) {

        for (final IPageAnalyzer analyzer : analyzers) {
            if (analyzer.matches(page)) return true;
        }

        return false;
    }
}
