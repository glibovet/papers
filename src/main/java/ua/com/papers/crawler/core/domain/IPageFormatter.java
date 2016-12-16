package ua.com.papers.crawler.core.domain;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 12/17/2016.
 */
@Service
@Validated
public interface IPageFormatter {

    /**
     * Maps 'raw' page representation into map which
     * represents page content parts
     *
     * @param id   page id
     * @param page page to map
     * @return instance of {@linkplain FormattedPage}
     */
    @NotNull
    FormattedPage format(@NotNull PageID id, @NotNull Page page);

}
