package ua.com.papers.services.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.convertors.Converter;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.PublicationEntity;

import ua.com.papers.services.utils.SessionUtils;

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

    @Override
    public PublicationEntity getPublicationById(int id) {
        return null;
    }

    @Override
    public Map<String, Object> getUserByIdMap(int userId, Set<String> fields) throws NoSuchEntityException {
        return null;
    }
}
