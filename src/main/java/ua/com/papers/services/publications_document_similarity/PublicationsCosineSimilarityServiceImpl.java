package ua.com.papers.services.publications_document_similarity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.persistence.dao.repositories.PublicationsCosineSimilarityRepository;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;

@Service
public class PublicationsCosineSimilarityServiceImpl implements IPublicationsCosineSimilarityService {

    @Autowired
    private PublicationsCosineSimilarityRepository publicationsCosineSimilarityRepository;

    @Transactional
    public PublicationsCosineSimilarityEntity save(PublicationsCosineSimilarityEntity entity) {
        entity = this.publicationsCosineSimilarityRepository.saveAndFlush(entity);
        if (entity == null) {
//            TODO
        }
        return entity;
    }
}
