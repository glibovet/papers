package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.UserEntity;

import java.util.List;

@Transactional(propagation= Propagation.REQUIRED)
public interface ContactsRepository extends JpaRepository<ContactEntity, Integer> {

    ContactEntity findByUserFromAndUserTo(UserEntity userFrom, UserEntity userTo);

    List<ContactEntity> findByUserToAndIsAccepted(UserEntity userTo, boolean isAccepted);
}
