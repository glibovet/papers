package ua.com.papers.services.publications_document_similarity;

import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;

public interface IPublicationsCosineSimilarityService {
    PublicationsCosineSimilarityEntity save(PublicationsCosineSimilarityEntity entity);
}
