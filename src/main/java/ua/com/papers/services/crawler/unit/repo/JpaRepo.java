package ua.com.papers.services.crawler.unit.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Максим on 2/12/2017.
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface JpaRepo extends JpaRepository<IndexEntity, String> {
}
