package ua.com.papers.crawler.core.analyze;

import lombok.Value;
import ua.com.papers.crawler.core.main.vo.PageID;
import ua.com.papers.crawler.settings.AnalyzeTemplate;
import ua.com.papers.crawler.settings.AnalyzeWeight;

import java.util.Set;

/**
 * <p>
 * Page analyze result
 * </p>
 * Created by Максим on 12/18/2016.
 */
@Value
public class Result {
    PageID id;
    Weight calculatedWeight;
    AnalyzeWeight minAcceptableWeight;
    Set<? extends AnalyzeTemplate> matchedRules;
    Set<? extends AnalyzeTemplate> rules;

    public boolean isMatching() {
        return calculatedWeight.getWeight() >= minAcceptableWeight.getWeight();
    }
}
