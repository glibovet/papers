package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.users.IUserService;
import ua.com.papers.services.utils.SessionUtils;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private SessionUtils sessionUtils;

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public ModelAndView profile(@PathVariable int id, ModelAndView modelAndView){
        try {
            UserEntity user = userService.getUserById(id);
            modelAndView.addObject("user", user);
            modelAndView.setViewName("user/profile");
        } catch (NoSuchEntityException e) {
            // return to 404
        }
        return modelAndView;
    }

    @RequestMapping(value = "/edit")
    public String editProfile(Model model) {
        UserEntity user = sessionUtils.getCurrentUser();
        if (user == null)
            return "/";
        model.addAttribute("user", user);
        model.addAttribute("userView", new UserView());
        return "user/edit";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateUser(@ModelAttribute("userView") UserView userView,
                             Model model
//                             @RequestParam(value = "photo") MultipartFile photo
                            ) throws NoSuchEntityException {
        UserEntity user = sessionUtils.getCurrentUser();
//        System.out.println(photo);
        if (user != null) {
            userView.setId(user.getId());
            System.out.println(userView);
            user = userService.update(userView);
            model.addAttribute("user", user);
            return "redirect:/users/"+user.getId();
        }
        return "/";
    }
}
