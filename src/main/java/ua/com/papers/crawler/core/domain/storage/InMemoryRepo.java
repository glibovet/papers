package ua.com.papers.crawler.core.domain.storage;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * In memory thread-safe repository build on the top
 * of hash map data structure
 * </p>
 * Created by Максим on 12/27/2016.
 */
@Repository
public final class InMemoryRepo implements IPageIndexRepository {

    private final Map<URL, Index> cache;

    private static InMemoryRepo instance;

    public static InMemoryRepo getInstance() {
        // double check idiom
        InMemoryRepo localInstance = instance;

        if (localInstance == null) {

            synchronized (InMemoryRepo.class) {

                localInstance = instance;

                if (localInstance == null) {
                    instance = localInstance = new InMemoryRepo();
                }
            }
        }
        return localInstance;
    }

    private InMemoryRepo() {
        cache = new HashMap<>(40);
    }

    @Override
    public boolean isIndexed(@NotNull URL url) {
        return cache.containsKey(Preconditions.checkNotNull(url));
    }

    @Nullable
    @Override
    public Index getIndex(@NotNull URL url) {
        return cache.get(Preconditions.checkNotNull(url));
    }

    @Override
    public void store(@NotNull Index index) {
        cache.put(index.getUrl(), index);
    }

    @Override
    public Iterator<Index> getIndexedPages() {
        return cache.values().iterator();
    }
}
