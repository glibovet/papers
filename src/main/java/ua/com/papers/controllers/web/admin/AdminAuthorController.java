package ua.com.papers.controllers.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by oleh_kurpiak on 10.10.2016.
 */
@Controller
@RequestMapping("/admin/authors")
public class AdminAuthorController {

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String allAuthors(){
        return "admin/authors/all_authors";
    }


    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createAuthor(Model model){
        model.addAttribute("id", 0);
        return "admin/authors/edit_author";
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String editAuthor(@PathVariable("id") int id,
                             Model model){
        model.addAttribute("id", id);
        return "admin/authors/edit_author";
    }
}
