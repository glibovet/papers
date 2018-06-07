package ua.com.papers.persistence.dao.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.pojo.entities.ChatEntity;
import ua.com.papers.pojo.entities.MessageEntity;

@Transactional(propagation= Propagation.REQUIRED)
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
}
