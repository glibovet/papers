package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.pojo.entities.PublisherEntity;

import java.util.List;

/**
 * Created by Andrii on 02.10.2016.
 */
public interface PublisherRepository extends JpaRepository<PublisherEntity, Integer>{
    List<PublisherEntity> findAll();
    Page<PublisherEntity> findAll(org.springframework.data.domain.Pageable pageable);
}
