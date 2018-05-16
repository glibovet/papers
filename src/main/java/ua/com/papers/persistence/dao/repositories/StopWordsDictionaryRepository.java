package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.pojo.entities.StopWordsDictionaryEntity;

import java.util.List;

@Transactional(propagation= Propagation.REQUIRED)
public interface StopWordsDictionaryRepository extends JpaRepository<StopWordsDictionaryEntity,Integer> {
    List<StopWordsDictionaryEntity> findAll();

    @Query("SELECT s FROM StopWordsDictionaryEntity s WHERE s.word =?1")
    List<StopWordsDictionaryEntity> getDictionaryItemsByWord(String word);
}
