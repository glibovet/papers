package ua.com.papers.controllers.web;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.view.SearchUsersView;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.users.IUserService;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.storage.IStorageService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    public String profile(@PathVariable int id, Model model){
        try {
            UserEntity currentUser = sessionUtils.getCurrentUser();
            UserEntity user = userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("currentUser", currentUser);
            ContactEntity contact = userService.getContactByUsers(currentUser, user);
            model.addAttribute("contact", contact);
        } catch (NoSuchEntityException e) {
            return "/";
        }
        return "user/profile";
    }

    @RequestMapping(value = "/edit")
    public String editProfile(Model model) throws NoSuchEntityException {
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
        if (user == null) {
            return "/";
        }
        if(StringUtils.isEmpty(userView.getName()) || StringUtils.isEmpty(userView.getLastName())){
            model.addAttribute("error", "Будь ласка, вкажіть ім'я та прізвище");
            model.addAttribute("user", user);
            return "user/edit";
        }
        userView.setId(user.getId());
        System.out.println(userView);
        user = userService.update(userView);
        storageService.uploadProfileImage(user, photo);
        return "redirect:/users/"+user.getId();
    }

    @RequestMapping(value = "/image/{id}")
    @ResponseBody
    public byte[] getProfileImage(@PathVariable(value = "id") int userId) throws IOException {
        return storageService.getProfileImage(userId);
    }

    @RequestMapping(value = "/contacts")
    public String allContacts(Model model,
                              @ModelAttribute("searchUsersView") SearchUsersView searchUsersView) {
        UserEntity user = sessionUtils.getCurrentUser();
        if (user == null)
            return "/";
        model.addAttribute("currentUser", user);
        Set<UserEntity> contacts = userService.getAcceptedContacts(user);
        System.out.println("contacts "+ contacts);
        model.addAttribute("contacts", contacts);
        model.addAttribute("searchUsersView", searchUsersView);
        System.out.println("searchUsersView "+ searchUsersView);
        List<ContactEntity> receivedContactRequests = userService.getReceivedContactRequests(user);
        System.out.println("receivedContactRequests "+ receivedContactRequests);
        model.addAttribute("receivedContactRequests", receivedContactRequests);
        if(searchUsersView.getName() != null || searchUsersView.getLastName() != null) {
            List<UserEntity> searchResults = userService.findByNames(searchUsersView.getName(), searchUsersView.getLastName());
            System.out.println("searchResults "+ searchResults);
//            for(UserEntity contact : contacts){
//                if(searchResults.contains(contact)){
//                    searchResults.remove(contact);
//                }
//            }
//            System.out.println("searchResults "+ searchResults);
            model.addAttribute("searchResults", searchResults);
        }
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

    @RequestMapping(value = {"/add-contact"}, method = RequestMethod.POST)
    public String addContact(@RequestParam(value = "userId") int userId, Model model) throws NoSuchEntityException {
        UserEntity user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "user/addContact";
    }

    @RequestMapping(value = {"/send-request"}, method = RequestMethod.POST)
    public String sendContactRequest(Model model,
                                     @RequestParam(value = "attachment") MultipartFile attachment,
                                     @RequestParam(value = "message") String message,
                                     @RequestParam(value = "id") int id) throws NoSuchEntityException, IOException, ServiceErrorException {
        UserEntity currentUser = sessionUtils.getCurrentUser();
        UserEntity user = userService.getUserById(id);
        if(currentUser.getRoleEntity().getName().equals(RolesEnum.student) &&
                user.getRoleEntity().getName().equals(RolesEnum.scientist) &&
                attachment.isEmpty()){
            model.addAttribute("error", "Ви маєте додати документ");
            model.addAttribute("user", user);
            model.addAttribute("message", message);
            return "user/addContact";
        }
        if(userService.isConnected(sessionUtils.getCurrentUser().getId(), id) ){
            return "redirect:/users/"+id;
        }
        ContactEntity contactEntity = userService.createContactRequest(currentUser, user, message, attachment);
        return "redirect:/users/"+id;
    }

    @RequestMapping(value = {"/delete-contact"}, method = RequestMethod.POST)
    public String deleteContact(@RequestParam(value = "contactId") int contactId, Model model) throws NoSuchEntityException {
        ContactEntity contact = userService.getContactById(contactId);
        userService.deleteContact(contact);
        return "redirect:/users/"+contact.getUserFrom().getId();
    }

    @RequestMapping(value = {"/accept-contact"}, method = RequestMethod.POST)
    public String acceptContact(@RequestParam(value = "contactId") int contactId, Model model) throws IOException {
        UserEntity user = sessionUtils.getCurrentUser();
        ContactEntity contact = userService.getContactById(contactId);
        if(contact == null || user.getId() != contact.getUserTo().getId()){
            return "redirect:/users/"+user.getId();
        }
        userService.acceptContactRequest(contact);
        return "redirect:/users/"+contact.getUserFrom().getId();
    }

    @RequestMapping(value = "/attachment/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public void getContactRequestAttachment(HttpServletResponse response,
                                            @PathVariable(value = "id") int contactId) throws IOException {
        ContactEntity contact = userService.getContactById(contactId);
        UserEntity user = sessionUtils.getCurrentUser();
        if(contact == null ||
                (contact.getUserTo().getId() != user.getId() && contact.getUserFrom().getId() != user.getId())){
            return;
        }
        storageService.getContactAttachment(response, contact);
    }

}
