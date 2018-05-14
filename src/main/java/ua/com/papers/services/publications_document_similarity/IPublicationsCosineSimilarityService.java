package ua.com.papers.services.publications_document_similarity;

import org.springframework.data.domain.Pageable;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;

import java.util.List;

public interface IPublicationsCosineSimilarityService {
    PublicationsCosineSimilarityEntity save(PublicationsCosineSimilarityEntity entity);
    void deleteAll();
    List<PublicationsCosineSimilarityEntity> findSimilar(PublicationEntity publication, Pageable pageable);
}
