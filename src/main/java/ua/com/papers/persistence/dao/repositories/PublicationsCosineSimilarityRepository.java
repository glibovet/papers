package ua.com.papers.persistence.dao.repositories;

import org.jboss.logging.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;

import java.util.List;

@Transactional(propagation= Propagation.REQUIRED)
public interface PublicationsCosineSimilarityRepository extends JpaRepository<PublicationsCosineSimilarityEntity,Integer> {
    List<PublicationsCosineSimilarityEntity> findAll();

    @Query("SELECT pcs FROM PublicationsCosineSimilarityEntity pcs WHERE pcs.publication1 =?1 OR pcs.publication2 =?1 ORDER BY value DESC")
    List<PublicationsCosineSimilarityEntity> getSimilarPageable(PublicationEntity publication, Pageable pageable);
}
