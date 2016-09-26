package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.services.publications.IPublicationService;

import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 26.09.2016.
 */
@Controller
@RequestMapping(value = "/api/publications")
public class PublicationApiController {

    @Autowired
    private ResponseFactory responseFactory;

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
        return responseFactory.get(publicationService.getUserByIdMap(userId, fields));
    }

}
