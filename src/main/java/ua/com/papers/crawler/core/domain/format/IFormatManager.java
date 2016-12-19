package ua.com.papers.crawler.core.domain.format;

import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/19/2016.
 */
@Validated
public interface IFormatManager {

    void processPage(@NotNull PageID pageID, @NotNull Page page);

}
