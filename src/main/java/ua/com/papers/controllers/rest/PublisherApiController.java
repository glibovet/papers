package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.services.utils.SessionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Controller
@RequestMapping(value = "/api/publisher")
public class PublisherApiController {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private IPublisherService publisherService;

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
            @PathVariable("id") int userId,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Publisher.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(publisherService.getPublisherMapById(userId, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getAuthors(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Publisher.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(publisherService.getPublishersMap(offset, limit, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody Response<Integer>
    createAuthor(
            @RequestBody PublisherView view
    ) throws PapersException {
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(publisherService.createPublisher(view));
    }

    @RequestMapping(
            value = "/"
            , method = RequestMethod.POST)
    public
    @ResponseBody
    Response<Integer> save(
            @RequestBody PublisherView view) throws PapersException {
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(publisherService.updatePublisher(view));
    }
}
