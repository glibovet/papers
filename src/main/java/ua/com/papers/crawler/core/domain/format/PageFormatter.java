package ua.com.papers.crawler.core.domain.format;

import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/17/2016.
 */
public class PageFormatter implements IPageFormatter {



    @Override
    public FormattedPage format(@NotNull PageID id, @NotNull Page page) {



        return new FormattedPage(id, null);
    }
}
