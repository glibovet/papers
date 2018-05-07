package ua.com.papers.crawler.core.storage;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;

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
     * <p>
     * Class which represents indexed page
     * </p>
     * Created by Максим on 12/27/2016.
     */
    @Value
    class Index {

        DateTime lastVisit;
        String contentHash;
        URL url;

        public Index(@NotNull DateTime lastVisit, @NotNull URL url, @NotNull String contentHash) {
            this.lastVisit = Preconditions.checkNotNull(lastVisit);
            this.url = Preconditions.checkNotNull(url);
            this.contentHash = Preconditions.checkNotNull(contentHash);
        }
    }

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
    Optional<Index> getIndex(@NotNull URL url);

    /**
     * Stores index result in repository
     *
     * @param index indexed page to store
     */
    void store(@NotNull Index index);

    Iterator<Index> indexedPagesIterator();

}
