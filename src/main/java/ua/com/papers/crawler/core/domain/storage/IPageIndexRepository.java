package ua.com.papers.crawler.core.domain.storage;

import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * <p>
 * Repository contract which provides client
 * with indexed pages and allows to store them.
 * Each indexed page has
 * its own visit date and url
 * </p>
 * Created by Максим on 12/27/2016.
 *
 * @see Index
 */
@Validated
public interface IPageIndexRepository {

    /**
     * checks whether page was indexed
     *
     * @param url url to check
     * @return true if page was indexed
     */
    boolean isIndexed(@NotNull URL url);

    /**
     * Returns indexed result if any
     *
     * @param url url of page to get
     * @return instance of {@linkplain Index}
     * if repository contains such indexed page url and null
     * in another case
     */
    @Nullable
    Index getIndex(@NotNull URL url);

    /**
     * Stores index result in repository
     *
     * @param index indexed page to store
     */
    void store(@NotNull Index index);

    ICursor<Index> getIndexedPages();

}
