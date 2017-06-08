package ua.com.papers.controllers.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Oleh on 17.12.2016.
 */
@Controller
@RequestMapping("/admin/publications")
public class AdminPublicationController {

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String all(){
        return "admin/publications/all";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model){
        model.addAttribute("id", 0);
        return "admin/publications/edit";
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String edit(@PathVariable("id") int id,
                                Model model){
        model.addAttribute("id", id);
        return "admin/publications/edit";
    }

}
