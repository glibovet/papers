package ua.com.papers.crawler.core.format;

import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Максим on 12/17/2016.
 */
@Validated
public interface IPageFormatter {

    /**
     * Maps 'raw' page representation into map which
     * represents page content parts
     *
     * @param id   page id
     * @param page page to map
     * @return the list of {@linkplain RawContent}
     */
    @NotNull
    List<RawContent> format(@NotNull PageID id, @NotNull Page page);

}
