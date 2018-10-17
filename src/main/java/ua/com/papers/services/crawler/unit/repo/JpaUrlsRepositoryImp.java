package ua.com.papers.services.crawler.unit.repo;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.crawler.core.main.model.PageStatus;
import ua.com.papers.crawler.core.storage.UrlsRepository;
import ua.com.papers.crawler.settings.JobId;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Максим on 2/12/2017.
 */
@Log
@Repository
public class JpaUrlsRepositoryImp implements UrlsRepository {

    private static final String TRANSACTION = "jobsTransaction";

    private final JpaUrlsRepository repository;

    private final class UrlsIterator implements Iterator<URL> {
        @Nullable
        private JobEntity next;
        private final JobId job;

        UrlsIterator(@NonNull JobId job) {
            this.job = job;
            this.next = queryNext();
        }

        @Override
        public synchronized boolean hasNext() {
            return next != null;
        }

        @Override
        @SneakyThrows(MalformedURLException.class)
        @Transactional(TRANSACTION)
        public synchronized URL next() {
            val current = next;

            next = queryNext();
            return current == null ? null : new URL(current.url);
        }

        @Nullable
        private JobEntity queryNext() {

            val jobs = repository.findFirstByStatusAndJob(PageStatus.PENDING, job.getId(), new PageRequest(0, 1));

            if (jobs != null && !jobs.isEmpty()) {
                jobs.get(0).setStatus(PageStatus.PROCESSING);
                repository.saveAndFlush(jobs.get(0));
                return jobs.get(0);
            }

            return null;
        }

    }

    @Autowired
    public JpaUrlsRepositoryImp(JpaUrlsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(TRANSACTION)
    public void store(URL url, JobId job, PageStatus status) {
        repository.saveAndFlush(new JobEntity(url.toExternalForm(), new Date(System.currentTimeMillis()), status, job.getId()));
    }

    @Override
    @Transactional(TRANSACTION)
    public void add(URL url, JobId job, PageStatus status) {
        if (!repository.exists(url.toExternalForm())) {
            store(url, job, status);
        }
    }

    @Override
    @Transactional(TRANSACTION)
    public void store(JobId job, PageStatus status) {
        repository.updateStatusForAll(status, job.getId());
    }

    @Override
    @Transactional(TRANSACTION)
    public void store(Collection<? extends URL> urls, JobId job, PageStatus status) {
        for (val u : urls) {
            repository.saveAndFlush(new JobEntity(u.toExternalForm(), new Date(System.currentTimeMillis()), status, job.getId()));
        }
    }

    @Override
    public Iterator<URL> urlsIterator(JobId job) {
        return new UrlsIterator(job);
    }

}
