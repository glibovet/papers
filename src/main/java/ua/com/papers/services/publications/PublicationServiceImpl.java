package ua.com.papers.services.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.pojo.entities.PublicationEntity;

import ua.com.papers.services.utils.SessionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 26.09.2016.
 */
@Service
public class PublicationServiceImpl implements IPublicationService{

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private Converter<PublicationEntity> publicationConverter;

    @Autowired
    private PublicationRepository publicationRepository;

    @Override
    @Transactional
    public PublicationEntity getPublicationById(int id) throws NoSuchEntityException {
        PublicationEntity publication = publicationRepository.findOne(id);
        if (publication == null)
            throw new NoSuchEntityException("publication", "id: " + id);
        return publication;
    }

    @Override
    @Transactional
    public Map<String, Object> getPublicationByIdMap(int id, Set<String> fields) throws NoSuchEntityException {
        return publicationConverter.convert(getPublicationById(id),fields);
    }

    @Override
    @Transactional
    public List<PublicationEntity> getPublications(int offset, int limit) throws NoSuchEntityException {
        Page<PublicationEntity> list = publicationRepository.findAll(new PageRequest(offset/limit,limit));
        if(list == null || list.getContent().isEmpty())
            throw new NoSuchEntityException("publication", String.format("[offset: %d, limit: %d]", offset, limit));
        return list.getContent();
    }

    @Override
    public List<Map<String, Object>> getPublicationsMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException {
        return publicationConverter.convert(getPublications(offset, limit), fields);
    }

}
