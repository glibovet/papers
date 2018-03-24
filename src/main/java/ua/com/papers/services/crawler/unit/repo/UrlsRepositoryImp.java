package ua.com.papers.services.crawler.unit.repo;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.crawler.core.storage.UrlsRepository;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Created by Максим on 2/12/2017.
 */
@Log
public class UrlsRepositoryImp implements UrlsRepository {

    private final EntityManager entityManager;

    private static final class UrlsIterator implements Iterator<URL> {
        private final EntityManager entityManager;
        private final TypedQuery<JobEntity> query;
        @Nullable
        private JobEntity next;

        UrlsIterator(EntityManager entityManager) {
            this.entityManager = entityManager;
            this.query = entityManager.createQuery("SELECT j FROM JobEntity j WHERE j.status = :status", JobEntity.class)
                    .setParameter("status", JobStatus.PENDING)
                    .setMaxResults(1);

            this.next = queryNext();
        }

        @Override
        public synchronized boolean hasNext() {
            return next != null;
        }

        @Override
        @SneakyThrows(MalformedURLException.class)
        public synchronized URL next() {
            val current = next;

            next = queryNext();
            return current == null ? null : new URL(current.url);
        }

        @Nullable
        private JobEntity queryNext() {
            try {
                entityManager.getTransaction().begin();

                try {
                    val job = query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();

                    job.setStatus(JobStatus.PROCESSING);

                    return entityManager.merge(job);
                } catch (final NoResultException nre) {
                    log.log(Level.INFO, "No job pending jobs found, finishing", nre);
                    return null;
                } finally {
                    entityManager.getTransaction().commit();
                }
            } catch (final Throwable th) {
                log.log(Level.SEVERE, "Error occurred while querying next job", th);
                entityManager.getTransaction().rollback();
                throw th;
            }
        }

    }

    public UrlsRepositoryImp(EntityManager managerFactoryBean) {
        this.entityManager = managerFactoryBean;
    }

    @Override
    public void storePending(URL url) {
        try {
            entityManager.getTransaction().begin();

            if (entityManager.find(JobEntity.class, url.toExternalForm(), LockModeType.PESSIMISTIC_WRITE) == null) {
                entityManager.merge(new JobEntity(url.toExternalForm(), new Date(System.currentTimeMillis()), JobStatus.PENDING));
            }

            entityManager.getTransaction().commit();
        } catch (final Throwable th) {
            log.log(Level.SEVERE, "Error occurred while querying next job", th);
            entityManager.getTransaction().rollback();
            throw th;
        }
    }

    private void insert(@NotNull URL url, @Nullable JobStatus status) {
        if (entityManager.find(JobEntity.class, url.toExternalForm(), LockModeType.PESSIMISTIC_WRITE) == null) {
            entityManager.merge(new JobEntity(url.toExternalForm(), new Date(System.currentTimeMillis()), JobStatus.PENDING));
        }
    }

    @Override
    public void storePending(Collection<? extends URL> urls) {
        try {
            entityManager.getTransaction().begin();

            for(val u : urls) {
                if (entityManager.find(JobEntity.class, u.toExternalForm(), LockModeType.PESSIMISTIC_WRITE) == null) {
                    entityManager.merge(new JobEntity(u.toExternalForm(), new Date(System.currentTimeMillis()), JobStatus.PENDING));
                }
            }

            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (final Throwable th) {
            log.log(Level.SEVERE, "Error occurred while performing batch insert", th);
            entityManager.getTransaction().rollback();
            throw th;
        }
    }

    @Override
    public void storeProcessing(URL url) {

    }

    @Override
    public Iterator<URL> pendingUrlsIterator() {
        return new UrlsIterator(entityManager);
    }

}
