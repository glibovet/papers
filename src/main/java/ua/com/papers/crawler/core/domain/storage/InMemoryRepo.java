package ua.com.papers.crawler.core.domain.storage;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    private final ReentrantReadWriteLock lock;

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
        lock = new ReentrantReadWriteLock();
        cache = new HashMap<>(40);
    }

    @Override
    public boolean isIndexed(@NotNull URL url) {
        lock.readLock().lock();

        try {
            return cache.containsKey(Preconditions.checkNotNull(url));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    @Override
    public Index getIndex(@NotNull URL url) {
        lock.readLock().lock();

        try {
            return cache.get(Preconditions.checkNotNull(url));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void store(@NotNull Index index) {
        lock.writeLock().lock();

        try {
            cache.put(index.getUrl(), index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<Index> getIndexedPages() {
        lock.readLock().lock();

        try {
            // copy values into temporary collection
            // in result each invocation of this method
            // will return a new iterator with copied values;
            // slow for a big collection
            return new ArrayList<>(cache.values()).iterator();
        } finally {
            lock.readLock().unlock();
        }
    }
}
