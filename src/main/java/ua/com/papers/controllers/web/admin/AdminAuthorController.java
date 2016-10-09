package ua.com.papers.controllers.web.admin;

import org.springframework.stereotype.Controller;
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

}
