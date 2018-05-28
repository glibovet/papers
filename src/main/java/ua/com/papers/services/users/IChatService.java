package ua.com.papers.services.users;

import ua.com.papers.pojo.entities.ChatEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.MessageView;

public interface IChatService {

    ChatEntity getChatById(int chatId);

    MessageEntity createMessage (MessageView view, UserEntity user);
}
