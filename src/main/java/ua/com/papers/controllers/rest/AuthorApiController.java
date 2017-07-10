package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.exceptions.service_error.AuthRequiredException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.utils.SessionUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 28.09.2016.
 */

@Controller
@RequestMapping(value = "/api/authors")
public class AuthorApiController {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private IAuthorService authorService;

    @Autowired
    private SessionUtils sessionUtils;

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Response<Map<String, Object>>
    getAuthor(
            @PathVariable("id") int id,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Author.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorMapById(id, fields));
    }

    @RequestMapping(
            value = "/master/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Response<Map<String, Object>>
    getMasterAuthor(
            @PathVariable("id") int id,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.AuthorMaster.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorMasterMapId(id, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getAuthors(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Author.DEFAULT) Set<String> fields,
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorsMap(offset, limit, fields, restrict));
    }

    @RequestMapping(
            value = "/count",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<Integer>
    getAuthorsCount(
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict
    ) throws PapersException {
        return responseFactory.get(authorService.countAuthors(restrict));
    }

    @RequestMapping(
            value = "/master/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getAuthorsMasters(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.AuthorMaster.DEFAULT) Set<String> fields,
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict
    ) throws PapersException {
        return responseFactory.get(authorService.getAuthorsMastersMap(offset, limit, fields, restrict));
    }

    @RequestMapping(
            value = "/master/count",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<Integer>
    getAuthorsMastersCount(
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict
    ) throws PapersException {
        return responseFactory.get(authorService.countAuthorsMaster(restrict));
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
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
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
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(authorService.createAuthorMaster(view));
    }

    @RequestMapping(
            value = "/"
            , method = RequestMethod.POST)
    public
    @ResponseBody
    Response<Integer> save(
            @RequestBody AuthorView authorView) throws PapersException {
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(authorService.updateAuthor(authorView));
    }

    @RequestMapping(
            value = "/master/"
            , method = RequestMethod.POST)
    public
    @ResponseBody
    Response<Integer> saveMaster(
            @RequestBody AuthorMasterView view) throws PapersException {
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(authorService.updateAuthorMaster(view));
    }

    @RequestMapping(
            value = "/{id}"
            , method = RequestMethod.DELETE
    )
    public
    void deleteAuthor(
            @PathVariable("id") int id,
            HttpServletResponse response
    ) throws PapersException{
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        authorService.deleteAuthor(id);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @RequestMapping(
            value = "/master/{id}"
            , method = RequestMethod.DELETE
    )
    public
    void deleteMasterAuthor(
            @PathVariable("id") int id,
            HttpServletResponse response
    ) throws PapersException{
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        authorService.deleteMasterAuthor(id);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public @ResponseBody Response<List<Map<String, Object>>>
    getAuthorsBySearchRequest(
            @RequestParam(value = "fields", required = false, defaultValue = Fields.AuthorDTO.DEFAULT) Set<String> fields,
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict
    ) throws PapersException {
        return responseFactory.get(authorService.searchAuthors(fields, restrict));
    }
}
