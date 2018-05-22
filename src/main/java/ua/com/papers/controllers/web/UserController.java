package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.view.SearchUsersView;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.users.IUserService;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.storage.IStorageService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private SessionUtils sessionUtils;
    @Autowired
    private IStorageService storageService;

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public ModelAndView profile(@PathVariable int id, ModelAndView modelAndView){
        try {
            UserEntity user = userService.getUserById(id);
            UserEntity currentUser = sessionUtils.getCurrentUser();
            modelAndView.addObject("user", user);
            ContactEntity contact = userService.getContactByUsers(currentUser, user);
            modelAndView.addObject("contact", contact);
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
                             Model model,
                             @RequestParam(value = "photo") MultipartFile photo
                            ) throws NoSuchEntityException, ValidationException, IOException, ServiceErrorException {
        UserEntity user = sessionUtils.getCurrentUser();
        if (user != null) {
            userView.setId(user.getId());
            System.out.println(userView);
            user = userService.update(userView);
            storageService.uploadProfileImage(user, photo);
            return "redirect:/users/"+user.getId();
        }
        return "/";
    }

    @RequestMapping(value = "/image/{id}")
    @ResponseBody
    public byte[] getProfileImage(@PathVariable(value = "id") int userId) throws IOException, NoSuchEntityException {
        return storageService.getProfileImage(userId);
    }

    @RequestMapping(value = "/contacts")
    public String allContacts(Model model) {
        UserEntity user = sessionUtils.getCurrentUser();
        if (user == null)
            return "/";
        model.addAttribute("user", user);
        model.addAttribute("contacts", userService.getAcceptedContacts(user));
        model.addAttribute("searchUsersView", new SearchUsersView());
        return "user/contacts";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchUsers(Model model,
                              @ModelAttribute("searchUsersView") SearchUsersView searchUsersView) {
        UserEntity user = sessionUtils.getCurrentUser();
        if (user == null)
            return "/";
        List<UserEntity> users = userService.findByNames(searchUsersView.getName(), searchUsersView.getLastName());
        model.addAttribute("user", user);
        model.addAttribute("contacts", users);
        model.addAttribute("searchUsersView", searchUsersView);
        return "user/contacts";
    }

    @RequestMapping(value = {"/add-contact/{id}"}, method = RequestMethod.GET)
    public String addContact(@PathVariable int id, Model model){
        try {
            UserEntity user = userService.getUserById(id);
            model.addAttribute("user", user);
        } catch (NoSuchEntityException e) {
            // return to 404
            return "/";
        }
        return "user/addContact";
    }

    @RequestMapping(value = {"/send-request"}, method = RequestMethod.POST)
    public String sendContactRequest(Model model,
                                     @RequestParam(value = "attachment") MultipartFile attachment,
                                     @RequestParam(value = "message") String message,
                                     @RequestParam(value = "id") int id) throws NoSuchEntityException, IOException, ServiceErrorException {
        System.out.println(message);
        System.out.println(id);
        System.out.println(attachment.getOriginalFilename());
        UserEntity currentUser = sessionUtils.getCurrentUser();
        UserEntity user = userService.getUserById(id);
        if(userService.isConnected(sessionUtils.getCurrentUser().getId(), id) ){
            return "redirect:/users/"+id;
        }
        ContactEntity contactEntity = userService.createContactRequest(currentUser, user, message, attachment);
        System.out.println(contactEntity);
        return "redirect:/users/"+id;
    }

    @RequestMapping(value = {"/delete-contact/{id}"}, method = RequestMethod.GET)
    public String deleteContact(@PathVariable int id, Model model){
        try {
            UserEntity user = userService.getUserById(id);
            userService.deleteContact(sessionUtils.getCurrentUser(), user);
        } catch (NoSuchEntityException e) {
            // return to 404
            return "/";
        }
        return "redirect:/users/"+id;
    }


}
