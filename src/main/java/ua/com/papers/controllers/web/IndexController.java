package ua.com.papers.controllers.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Andrii on 27.07.2016.
 */
@Controller
public class IndexController {

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String indexPage(Model model){
        return "index/index";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/sign_up", method = RequestMethod.GET)
    public String signUp(){
        return "auth/sign_up";
    }
}
