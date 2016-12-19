package ua.com.papers.crawler.core.domain.analyze;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.elasticsearch.common.collect.Tuple;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.settings.PageSetting;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/18/2016.
 */
@Value
public class AnalyzeManager implements IAnalyzeManager {

    Map<PageSetting, ? extends IPageAnalyzer> analyzers;

    public AnalyzeManager(@NotNull Map<PageSetting, ? extends IPageAnalyzer> analyzers) {
        this.analyzers = Collections.unmodifiableMap(Preconditions.checkNotNull(analyzers));
    }

    @NotNull
    @Override
    public Collection<Result> analyze(@NotNull Page page) {
        return analyzers.entrySet()
                .stream()
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue().analyze(page)))
                .filter(t -> t.v2().getResultWeight() >= t.v1().getMinWeight())
                .map(Tuple::v2)
                .collect(Collectors.toList());
    }
}