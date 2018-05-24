package ua.com.papers.controllers.web;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.pojo.chat.Greeting;
import ua.com.papers.pojo.chat.HelloMessage;

@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public String mainChatPage(@PathVariable(value = "id") int id,
                                     Model model) {
        System.out.println("chat "+id);
        model.addAttribute("chatId", id);
        return "user/chat";
    }

//    @MessageMapping("/chat/{chatId}")
//    @SendTo("/topic/messages/{chatId}")
//    public String send(@DestinationVariable(value = "chatId") String chatId,
//                             String message) {
//        System.out.println("message "+message);
//        //call service to store new message
//        return message;
//
//
//    }

    @MessageMapping("/chat")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        System.out.println("controller message "+message.getName());
        Thread.sleep(3000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }

}