package ua.com.papers.services.users;

import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.entities.ChatEntity;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.MessageView;

import java.io.IOException;

public interface IChatService {

    ChatEntity getChatById(int chatId);

    MessageEntity createMessage (MessageView view, UserEntity user) throws IOException;

    ChatEntity getChatByUsers (UserEntity fist, UserEntity second);

    ChatEntity createChat (UserEntity initial, UserEntity second);

    MessageEntity createMessage (ChatEntity chat, UserEntity user, String text, MultipartFile attachment) throws IOException, ServiceErrorException;

    MessageEntity update(MessageEntity message);

    MessageEntity createMessageFromContactRequest (ChatEntity chat, ContactEntity contact) throws IOException;

    MessageEntity getMessageById(int messageId);
}
