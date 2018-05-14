package ua.com.papers.services.publications_document_similarity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.persistence.dao.repositories.PublicationsCosineSimilarityRepository;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.services.publications.IPublicationService;

import java.util.List;

@Service
public class PublicationsCosineSimilarityServiceImpl implements IPublicationsCosineSimilarityService {

    @Autowired
    private PublicationsCosineSimilarityRepository publicationsCosineSimilarityRepository;

    @Transactional
    public PublicationsCosineSimilarityEntity save(PublicationsCosineSimilarityEntity entity) {
        entity = this.publicationsCosineSimilarityRepository.saveAndFlush(entity);
        return entity;
    }

    @Transactional
    public void deleteAll() {
        this.publicationsCosineSimilarityRepository.deleteAll();
    }

    @Transactional
    public List<PublicationsCosineSimilarityEntity> findSimilar(PublicationEntity publication, Pageable pageable) {
        return this.publicationsCosineSimilarityRepository.getSimilarPageable(publication, pageable);
    }
}
