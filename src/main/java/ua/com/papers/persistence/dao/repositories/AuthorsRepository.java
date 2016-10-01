package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.pojo.entities.AuthorEntity;

import java.util.List;

/**
 * Created by Andrii on 28.09.2016.
 */
public interface AuthorsRepository extends JpaRepository<AuthorEntity,Integer>{
    List<AuthorEntity> findAll();
    Page<AuthorEntity> findAll(org.springframework.data.domain.Pageable pageable);
}
