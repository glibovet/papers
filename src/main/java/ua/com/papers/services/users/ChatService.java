package ua.com.papers.services.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.persistence.dao.repositories.ChatRepository;
import ua.com.papers.persistence.dao.repositories.MessageRepository;
import ua.com.papers.persistence.dao.repositories.UsersRepository;
import ua.com.papers.pojo.entities.ChatEntity;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.MessageView;
import ua.com.papers.storage.IStorageService;
import ua.com.papers.storage.impl.StorageServiceImpl;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChatService implements IChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private IStorageService storageService;

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

    @Override
    public MessageEntity createMessage (ChatEntity chat, UserEntity user, String text, MultipartFile attachment) throws IOException, ServiceErrorException {
        MessageEntity message = new MessageEntity();
        message.setUser(user);
        message.setText(text);
        message.setChat(chat);
        message.setDate(new Date());
        message = messageRepository.saveAndFlush(message);
        storageService.uploadMessageAttachment(message, attachment);
        return message;
    }

    public ChatEntity getChatByUsers (UserEntity fist, UserEntity second){
        for(ChatEntity chat : fist.getChats()){
            if(chat.getMembers().contains(second)){
                return chat;
            }
        }
        return null;
    }

    @Override
    public ChatEntity createChat (UserEntity initial, UserEntity second){
        ChatEntity chat = new ChatEntity();
        chat.setInitiatorUser(initial);
        Set<UserEntity> members = new HashSet<>(2);
        members.add(initial);
        members.add(second);
        chat.setMembers(members);
        initial.getChats().add(chat);
        second.getChats().add(chat);
        chat = chatRepository.saveAndFlush(chat);
        usersRepository.saveAndFlush(initial);
        usersRepository.saveAndFlush(second);
        return chat;
    }

    @Override
    public MessageEntity update(MessageEntity message) {
        messageRepository.saveAndFlush(message);
        return message;
    }
}
