package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.PublicationOrderEntity;

import java.util.List;

/**
 * Created by Andrii on 20.05.2017.
 */
@Transactional(propagation= Propagation.REQUIRED)
public interface PublicationOrderRepository extends JpaRepository<PublicationOrderEntity, Integer> {
    List<PublicationOrderEntity> findAll();
    Page<PublicationOrderEntity> findAll(org.springframework.data.domain.Pageable pageable);
}
