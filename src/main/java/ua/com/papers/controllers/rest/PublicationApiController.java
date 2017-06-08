package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.utils.SessionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 26.09.2016.
 */
@Controller
@RequestMapping(value = "/api/publication")
public class PublicationApiController {

    @Autowired
    private ResponseFactory responseFactory;
    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private IPublicationService publicationService;

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Response<Map<String, Object>> getPublication(
            @PathVariable("id") int userId,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Publication.DEFAULT) Set<String> fields
    ) throws NoSuchEntityException {
        return responseFactory.get(publicationService.getPublicationByIdMap(userId, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getPublications(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.User.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(publicationService.getPublicationsMap(offset, limit, fields, restrict));
    }

    @RequestMapping(
            value = "/count",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<Integer>
    countPublications(
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict
    ) throws PapersException {
        return responseFactory.get(publicationService.countPublications(restrict));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody Response<Integer>
    createPublication(
            @RequestBody PublicationView view
    ) throws PapersException {
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(publicationService.createPublication(view));
    }

    @RequestMapping(
            value = "/"
            , method = RequestMethod.POST)
    public
    @ResponseBody
    Response<Integer> save(
            @RequestBody PublicationView view) throws PapersException {
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(publicationService.updatePublication(view));
    }

}
