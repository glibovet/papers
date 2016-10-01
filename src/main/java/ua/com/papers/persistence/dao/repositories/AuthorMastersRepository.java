package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.pojo.entities.AuthorMasterEntity;

import java.util.List;

/**
 * Created by Andrii on 28.09.2016.
 */
public interface AuthorMastersRepository extends JpaRepository<AuthorMasterEntity,Integer>{
    List<AuthorMasterEntity> findAll();
    Page<AuthorMasterEntity> findAll(Pageable pageable);
}
