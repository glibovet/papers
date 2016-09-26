package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.pojo.entities.UserEntity;

import java.util.List;

/**
 * Created by Andrii on 18.08.2016.
 */
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {

    public UserEntity findByEmail(String email);

    List<UserEntity> findAll();

    List<UserEntity> findAll(Sort sort);

    Page<UserEntity> findAll(Pageable pageable);
}
