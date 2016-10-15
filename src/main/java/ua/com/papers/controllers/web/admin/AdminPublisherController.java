package ua.com.papers.controllers.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by oleh_kurpiak on 15.10.2016.
 */
@Controller
@RequestMapping("/admin/publishers")
public class AdminPublisherController {

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String allPublishers(){
        return "admin/publishers/all_publishers";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createPublisher(Model model){
        model.addAttribute("id", 0);
        return "admin/publishers/edit_publisher";
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String editPublisher(@PathVariable("id") int id,
                             Model model){
        model.addAttribute("id", id);
        return "admin/publishers/edit_publisher";
    }
}
