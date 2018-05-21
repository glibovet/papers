package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.UserEntity;

import java.util.List;

/**
 * Created by Andrii on 18.08.2016.
 */
@Transactional(propagation= Propagation.REQUIRED)
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByEmail(String email);

    List<UserEntity> findAll();

    List<UserEntity> findAll(Sort sort);

    Page<UserEntity> findAll(Pageable pageable);

    @Query("Select u from UserEntity u where u.name = :name or u.lastName = :name")
    List<UserEntity> findByNames(@Param("name")String name);
}
