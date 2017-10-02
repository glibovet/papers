package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.pojo.view.PublicationOrderView;
import ua.com.papers.services.publications.IPublicationOrderService;
import ua.com.papers.services.utils.SessionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 20.05.2017.
 */
@Controller
@RequestMapping(value = "/api/publication/order")
public class PublicationOrderApiController {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private IPublicationOrderService service;

    @Autowired
    private SessionUtils sessionUtils;

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Response<Map<String, Object>>
    getPublicationOrder(
            @PathVariable("id") int id,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.PublicationOrder.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(service.getPublicationOrderMapById(id, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getPublicationOrders(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.PublicationOrder.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(service.getPublicationOrdersMap(offset, limit, fields, restrict));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody Response<Integer>
    create(
            @RequestBody PublicationOrderView view
    ) throws PapersException {
        return responseFactory.get(service.create(view));
    }

    @RequestMapping(
            value = "/"
            , method = RequestMethod.POST)
    public
    @ResponseBody
    Response<Integer> update(
            @RequestBody PublicationOrderView view) throws PapersException {
        sessionUtils.authorized();
        sessionUtils.userHasRole(RolesEnum.admin,RolesEnum.moderator);
        return responseFactory.get(service.update(view));
    }

    @RequestMapping(
            value = "/answer"
            , method = RequestMethod.POST)
    public
    @ResponseBody
    Response<Integer> answer(
            @RequestBody PublicationOrderView view) throws PapersException {
        sessionUtils.authorized();
        sessionUtils.userHasRole(RolesEnum.admin,RolesEnum.moderator);
        return responseFactory.get(service.answer(view));
    }

    @RequestMapping(
            value = "/count",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<Integer>
    countPublications(
            @RequestParam(value = "restrict", required = false, defaultValue = "") String restrict
    ) throws PapersException {
        return responseFactory.get(service.count(restrict));
    }

}
