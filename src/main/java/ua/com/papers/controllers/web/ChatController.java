package ua.com.papers.controllers.web;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.elasticsearch.gateway.AsyncShardFetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.entities.ChatEntity;
import ua.com.papers.pojo.entities.MessageEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.MessageView;
import ua.com.papers.services.users.IChatService;
import ua.com.papers.services.users.IUserService;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.storage.IStorageService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    @Autowired
    private IStorageService storageService;

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public String mainChatPage(@PathVariable(value = "id") int id,
                                     Model model) {
        System.out.println("chat "+id);
        UserEntity user = sessionUtils.getCurrentUser();
        ChatEntity chat = chatService.getChatById(id);
        Set<UserEntity> members = chat.getMembers();
        if(!members.contains(user)){
            return "redirect:/users/"+user.getId();
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
        model.addAttribute("currentUser", user);
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
        message.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(messageEntity.getDate()));
        return message;
    }

    @RequestMapping(value = {"/message"}, method = RequestMethod.POST)
    public String newMessage(Model model, @RequestParam(value = "userId") int userId) throws NoSuchEntityException {
        UserEntity user = userService.getUserById(userId);
        UserEntity currentUser = sessionUtils.getCurrentUser();
        ChatEntity chat = chatService.getChatByUsers(user, currentUser);
        if(chat != null){
            return "redirect:/chat/"+chat.getId();
        }
        model.addAttribute("user", user);
        return "user/message";
    }

    @RequestMapping(value = {"/send-message"}, method = RequestMethod.POST)
    public String sendMessage(Model model,
                                 @RequestParam(value = "attachment") MultipartFile attachment,
                                 @RequestParam(value = "message") String message,
                                 @RequestParam(value = "userId") int userId) throws NoSuchEntityException, IOException, ServiceErrorException {
        UserEntity currentUser = sessionUtils.getCurrentUser();
        UserEntity user = userService.getUserById(userId);
        ChatEntity chat = chatService.createChat(currentUser,user);
        chatService.createMessage(chat, currentUser, message, attachment);
        return "redirect:/chat/"+chat.getId();
    }

    @RequestMapping(value = "/message-attachment/{messageId}" , method = RequestMethod.GET)
    @ResponseBody
    public void getMessageAttachment(HttpServletResponse response,
                                     @PathVariable(value = "messageId") int messageId) throws IOException {
        MessageEntity message = chatService.getMessageById(messageId);
        UserEntity user = sessionUtils.getCurrentUser();
        if(message == null || !message.getChat().getMembers().contains(user)){
            response.sendRedirect("/index");
            return;
        }
        storageService.getMessageAttachment(response, message);
    }

//    @RequestMapping(value = {"/test/{message}"}, method = RequestMethod.GET)
//    public String testMessage(Model model, @PathVariable(value = "message") String message) throws NoSuchEntityException {
//        UserEntity user = userService.getUserById(2);
//        ChatEntity chat = chatService.getChatById(1);
//        MessageEntity messageEntity = chatService.createMessage(chat, user, message);
//        return "/index/index";
//    }

}
