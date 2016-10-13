package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.AuthorMasterEntity;

import java.util.List;

/**
 * Created by Andrii on 28.09.2016.
 */
@Transactional(propagation= Propagation.REQUIRED)
public interface AuthorMastersRepository extends JpaRepository<AuthorMasterEntity,Integer>{
    List<AuthorMasterEntity> findAll();
    Page<AuthorMasterEntity> findAll(Pageable pageable);
}
