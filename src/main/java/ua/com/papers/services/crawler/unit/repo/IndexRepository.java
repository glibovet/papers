package ua.com.papers.services.crawler.unit.repo;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.val;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.com.papers.crawler.core.storage.IPageIndexRepository;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Максим on 2/12/2017.
 */
@Log
@Deprecated
@Repository
public final class IndexRepository implements IPageIndexRepository {

    private final JpaIndexRepository indexRepository;

    @Autowired
    public IndexRepository(JpaIndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @Override
    public boolean isIndexed(@NotNull URL url) {
        return indexRepository.findOne(url.toExternalForm()) != null;
    }

    @Override
    public Optional<Index> getIndex(@NotNull URL url) {
        return Optional.ofNullable(indexRepository.findOne(url.toExternalForm())).map(IndexRepository::toIndex);
    }

    @Override
    public void store(@NotNull Index index) {
        indexRepository.saveAndFlush(IndexRepository.toEntity(index));
    }

    @Override
    public Iterator<Index> indexedPagesIterator() {
        val it = indexRepository.findAll().iterator();
        return new Iterator<Index>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Index next() {
                return IndexRepository.toIndex(it.next());
            }
        };
    }

    private static IndexEntity toEntity(Index index) {
        return new IndexEntity(index.getUrl().toExternalForm(),
                new Date(index.getLastVisit().toInstant().getMillis()),
                index.getContentHash());
    }

    @SneakyThrows(MalformedURLException.class)
    private static Index toIndex(IndexEntity entity) {
        return new Index(new DateTime(entity.getLastVisit().getTime()), new URL(entity.getUrl()), entity.getContentHash());
    }

}
