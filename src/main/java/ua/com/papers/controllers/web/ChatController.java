package ua.com.papers.controllers.web;

import org.elasticsearch.gateway.AsyncShardFetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.pojo.entities.ChatEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.MessageView;
import ua.com.papers.services.users.IChatService;
import ua.com.papers.services.users.IUserService;
import ua.com.papers.services.utils.SessionUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    @Autowired
    private IUserService userService;
    @Autowired
    private SessionUtils sessionUtils;
    @Autowired
    private IChatService chatService;

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public String mainChatPage(@PathVariable(value = "id") int id,
                                     Model model) {
        System.out.println("chat "+id);
        UserEntity user = sessionUtils.getCurrentUser();
        ChatEntity chat = chatService.getChatById(id);
        Set<UserEntity> members = chat.getMembers();
        if(!members.contains(user)){
            return "/";
        }
        List<MessageEntity> messages = chat.getMessages();
        System.out.println("messages "+messages);
        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", user);
        return "user/chat";
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String allChats(Model model) {
        UserEntity user = sessionUtils.getCurrentUser();
        Set<ChatEntity> chats = user.getChats();
        System.out.println(chats);
        model.addAttribute("chats", chats);
        return "user/allChats";
    }

    @MessageMapping("/papers/{chatId}")
    @SendTo("/topic/papers/{chatId}")
    public MessageView message(@DestinationVariable(value = "chatId") String chatId,
                                MessageView message) throws Exception {
        UserEntity user = userService.getUserById(message.getUserId());
        message.setUserName(user.getName());
        message.setUserLastName(user.getLastName());
        System.out.println("chatId "+chatId);
        System.out.println("message: "+ message);
        MessageEntity messageEntity = chatService.createMessage(message, user);
        message.setDate(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(messageEntity.getDate()));
        return message;
    }

}
