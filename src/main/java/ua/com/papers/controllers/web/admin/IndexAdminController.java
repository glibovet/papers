package ua.com.papers.controllers.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Andrii on 26.08.2016.
 */
@Controller
@RequestMapping("/admin/")
public class IndexAdminController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String indexPage(Model model){
        return "/admin/index";
    }
}
