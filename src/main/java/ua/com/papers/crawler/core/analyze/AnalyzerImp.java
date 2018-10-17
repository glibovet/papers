package ua.com.papers.crawler.core.analyze;

import lombok.NonNull;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.util.PageUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/18/2016.
 */
public final class AnalyzerImp implements Analyzer {

    private final Collection<? extends PageAnalyzer> analyzers;

    public AnalyzerImp(@NonNull Collection<? extends PageAnalyzer> analyzers) {
        this.analyzers = Collections.unmodifiableCollection(new ArrayList<>(analyzers));
    }

    @NotNull
    @Override
    public Set<Result> analyze(@NotNull Page page) {
        if (!PageUtils.canParse(page.getContentType())) {
            // you cannot analyze page which is can't be transformed into
            // text document, for example, mp3 track
            return Collections.emptySet();
        }

        return analyzers.stream().map(entry -> entry.analyze(page)).filter(Result::isMatching).collect(Collectors.toSet());
    }

    @Override
    public Set<Result> matchingResults(Page page) {
        return analyze(page).stream().filter(Result::isMatching).collect(Collectors.toSet());
    }
}
