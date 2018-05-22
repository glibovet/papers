package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.UserEntity;

@Transactional(propagation= Propagation.REQUIRED)
public interface ContactsRepository extends JpaRepository<ContactEntity, Integer> {

    ContactEntity findByUserFromAndUserTo(UserEntity userFrom, UserEntity userTo);
}
