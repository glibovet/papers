package ua.com.papers.crawler.core.select;

import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Set;

/**
 * Created by Максим on 12/18/2016.
 */
@Validated
public interface IUrlExtractor {

    @NotNull
    Set<URL> extract(@NotNull(message = "Page id == null") PageID id,
                     @NotNull(message = "Cannot extract urls from null page") Page page);

}
