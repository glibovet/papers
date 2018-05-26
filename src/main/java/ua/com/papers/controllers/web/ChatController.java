package ua.com.papers.controllers.web;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.pojo.view.MessageView;

import java.util.ArrayList;

@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public String mainChatPage(@PathVariable(value = "id") int id,
                                     Model model) {
        System.out.println("chat "+id);
        model.addAttribute("chatId", id);
        ArrayList<MessageView> mss = new ArrayList<>();
        MessageView ms = new MessageView();
        ms.setChatId(1);
        ms.setUserId(1);
        ms.setText("Ndjfgls jfdhkjdf hdfjkgh d fdsklhg jk sdfgj hdskjf sfd ghsd j dfjkgh  lsffdg lkj lk");
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        model.addAttribute("messages", mss);
        return "user/chat";
    }

    @RequestMapping(value = {"/all"}, method = RequestMethod.GET)
    public String allChats(Model model) {
        ArrayList<MessageView> mss = new ArrayList<>();
        MessageView ms = new MessageView();
        ms.setChatId(1);
        ms.setUserId(1);
        ms.setText("Ndjfgls jfdhkjdf hdfjkgh d fdsklhg jk sdfgj hdskjf sfd ghsd j dfjkgh  lsffdg lkj lk");
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        mss.add(ms);
        model.addAttribute("messages", mss);
        return "user/allChats";
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

    @MessageMapping("/papers/{chatId}")
    @SendTo("/topic/papers/{chatId}")
    public MessageView greeting(@DestinationVariable(value = "chatId") String chatId,
                                MessageView message) throws Exception {
        System.out.println("chatId "+chatId);

        Thread.sleep(3000); // simulated delay
        return message;
    }

}
