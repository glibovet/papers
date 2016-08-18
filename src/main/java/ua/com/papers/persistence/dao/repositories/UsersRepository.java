package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.persistence.entities.UserEntity;

/**
 * Created by Andrii on 18.08.2016.
 */
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {

    public UserEntity findByEmail(String email);
}
