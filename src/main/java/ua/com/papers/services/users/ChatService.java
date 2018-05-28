package ua.com.papers.services.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.persistence.dao.repositories.ChatRepository;
import ua.com.papers.persistence.dao.repositories.MessageRepository;
import ua.com.papers.pojo.entities.ChatEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.MessageView;

import java.util.Date;

@Service
public class ChatService implements IChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public ChatEntity getChatById(int chatId) {
        return chatRepository.findOne(chatId);
    }

    @Override
    public MessageEntity createMessage (MessageView view, UserEntity user){
        MessageEntity message = new MessageEntity();
        message.setUser(user);
        message.setText(view.getText());
        message.setChat(chatRepository.findOne(view.getChatId()));
        message.setDate(new Date());
        message = messageRepository.saveAndFlush(message);
        return message;
    }
}
