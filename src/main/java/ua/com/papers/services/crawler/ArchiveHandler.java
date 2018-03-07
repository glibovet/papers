package ua.com.papers.services.crawler;

import lombok.Value;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;

import java.util.logging.Level;

/**
 * <p>
 *     Handler for http://journals.uran.ua/index.php/1991-0177/issue/archive
 * </p>
 * Created by Максим on 6/2/2017.
 */
@Log
@Value
@PageHandler(id = 1)
public class ArchiveHandler {

    @PreHandle
    public void onPrepare(Page page) {
        log.log(Level.INFO, String.format("#onPrepare %s", page.getUrl()));
    }

    @PostHandle
    public void onPageEnd(Page page) {
        log.log(Level.INFO, String.format("#onPageEnd %s", page.getUrl()));
    }

}
