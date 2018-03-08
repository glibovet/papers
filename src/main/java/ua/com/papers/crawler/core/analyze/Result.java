package ua.com.papers.crawler.core.analyze;

import lombok.Value;
import ua.com.papers.crawler.core.domain.vo.PageID;

/**
 * <p>
 *     Page analyze result
 * </p>
 * Created by Максим on 12/18/2016.
 */
@Value
public class Result {
    PageID pageID;
    int resultWeight;
}
