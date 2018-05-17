package ua.com.papers.services.crawler.unit.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.crawler.core.main.model.PageStatus;

import java.util.List;

/**
 * Created by Максим on 2/12/2017.
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface JpaUrlsRepository extends JpaRepository<JobEntity, String> {

    List<JobEntity> findFirstByStatusAndJob(PageStatus status, String job, Pageable limit);

    List<JobEntity> findFirstByUrlAndJob(String url, String job, Pageable limit);

    @Modifying(clearAutomatically = true)
    @Query("update JobEntity e set e.status =:status where e.job =:job")
    void updateStatusForAll(@Param("status") PageStatus status, @Param("job") String job);

}
