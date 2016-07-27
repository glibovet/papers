package ua.com.papers.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Andrii on 27.07.2016.
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String indexPage(Model model){
        return "index";
    }
}
