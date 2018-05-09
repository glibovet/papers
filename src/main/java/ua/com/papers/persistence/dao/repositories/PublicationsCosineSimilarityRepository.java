package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;

import java.util.List;

@Transactional(propagation= Propagation.REQUIRED)
public interface PublicationsCosineSimilarityRepository extends JpaRepository<PublicationsCosineSimilarityEntity,Integer> {
    List<PublicationsCosineSimilarityEntity> findAll();
}
