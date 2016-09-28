package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.services.authors.IAuthorService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 28.09.2016.
 */

@Controller
@RequestMapping(value = "/api/autor")
public class AuthorApiController {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private IAuthorService authorService;

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Response<Map<String, Object>>
    getAuthor(
            @PathVariable("id") int userId,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Author.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorMapById(userId, fields));
    }

    @RequestMapping(
            value = "/master/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Response<Map<String, Object>>
    getMasterAuthor(
            @PathVariable("id") int userId,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.AuthorMaster.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorMasterMapId(userId, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getAuthors(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Author.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorsMap(offset, limit, fields));
    }

    @RequestMapping(
            value = "/master/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getAuthorsMasters(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Author.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorsMastersMap(offset, limit, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody Response<Integer>
    createAuthor(
            @RequestBody AuthorView view
    ) throws PapersException {
        return responseFactory.get(authorService.createAuthor(view));
    }

    @RequestMapping(
            value = "/master/",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody Response<Integer>
    createAuthorMaster(
            @RequestBody AuthorMasterView view
    ) throws PapersException {
        return responseFactory.get(authorService.createAuthorMaster(view));
    }

}
