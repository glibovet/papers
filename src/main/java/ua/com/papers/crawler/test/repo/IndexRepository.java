package ua.com.papers.crawler.test.repo;

import lombok.Value;
import lombok.val;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.papers.crawler.core.domain.storage.IPageIndexRepository;
import ua.com.papers.crawler.util.Url;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.sql.Date;
import java.util.Iterator;

/**
 * Created by Максим on 2/12/2017.
 */
@Repository
@Value
public final class IndexRepository implements IPageIndexRepository {

    JpaRepository<IndexEntity, String> repository;
    @Autowired
    public IndexRepository(JpaRepository<IndexEntity, String> repository) {
        this.repository = repository;
    }

    @Override
    public boolean isIndexed(@NotNull URL url) {
        return repository.findOne(url.toExternalForm()) != null;
    }

    @Nullable
    @Override
    public Index getIndex(@NotNull URL url) {
        return IndexRepository.toIndex(repository.findOne(url.toExternalForm()));
    }

    @Override
    public void store(@NotNull Index index) {
        repository.saveAndFlush(IndexRepository.toEntity(index));
    }

    @Override
    public Iterator<Index> getIndexedPages() {
        val it = repository.findAll().iterator();
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

    private static Index toIndex(IndexEntity entity) {
        return new Index(new DateTime(entity.getLastVisit().getTime()), new Url(entity.getUrl()).getUrl(), entity.getContentHash());
    }

}
