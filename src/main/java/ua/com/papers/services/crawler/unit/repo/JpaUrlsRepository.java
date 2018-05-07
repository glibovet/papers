package ua.com.papers.services.crawler.unit.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Максим on 2/12/2017.
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface JpaUrlsRepository extends JpaRepository<JobEntity, String> {

    List<JobEntity> findFirstByStatusAndJob(JobStatus status, String job, Pageable limit);

}
