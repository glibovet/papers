package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.neo4j.repository.GraphRepository;
import ua.com.papers.pojo.entities.PublicationReferencesEntity;

/**
 * Created by oleh_kurpiak on 15.09.2016.
 */
public interface PublicationReferencesRepository extends GraphRepository<PublicationReferencesEntity> {
}
