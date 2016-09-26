package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.pojo.entities.PublicationEntity;

import java.util.List;

/**
 * Created by Andrii on 26.09.2016.
 */
public interface PublicationRepository extends JpaRepository<PublicationEntity,Integer>{

    List<PublicationEntity> findAll();
    Page<PublicationEntity> findAll(Pageable pageable);
}
