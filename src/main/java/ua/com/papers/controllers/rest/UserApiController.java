package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.exceptions.bad_request.WrongPasswordException;
import ua.com.papers.exceptions.conflict.EmailExistsException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.users.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
@Controller
@RequestMapping(value = "/api/users")
public class UserApiController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ResponseFactory responseFactory;

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody Response<Map<String, Object>>
    getUser(
            @PathVariable("id") int userId,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.User.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(userService.getUserByIdMap(userId, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getUsers(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.User.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(userService.getUsersMap(offset, limit, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody Response<Integer>
    createUser(
            @RequestBody UserView view
    ) throws PapersException {
        return responseFactory.get(userService.create(view));
    }

    @RequestMapping(
            value = "/sign_in",
            method = RequestMethod.POST
    )
    public
    @ResponseBody Response<Boolean>
    signIn(
            @RequestBody UserView view
    ) throws PapersException {
        return responseFactory.get(userService.signInUser(view));
    }

    @RequestMapping(
            value = "/logout",
            method = RequestMethod.POST
    )
    public
    @ResponseBody Response<Boolean>
    logout(HttpServletRequest request, HttpServletResponse response){
        return responseFactory.get(userService.logoutUser(request, response));
    }
}
