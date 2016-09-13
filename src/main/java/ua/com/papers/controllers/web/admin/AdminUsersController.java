package ua.com.papers.controllers.web.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

/**
 * Created by oleh_kurpiak on 12.09.2016.
 */
@Controller
@RequestMapping(value = "/admin/users")
public class AdminUsersController {

    @RequestMapping(value = {"/", "/all"}, method = RequestMethod.GET)
    public String getAllUsers(Principal principal){
        return "admin/users/all_users";
    }

}
