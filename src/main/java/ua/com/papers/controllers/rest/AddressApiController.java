package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.pojo.view.AddressView;
import ua.com.papers.services.address.IAddressService;
import ua.com.papers.services.utils.SessionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Controller
@RequestMapping(value = "/api/address")
public class AddressApiController {
    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private IAddressService addressService;

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
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Address.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(addressService.getAddressMapById(id, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    public @ResponseBody Response<List<Map<String, Object>>>
    getAuthors(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "fields", required = false, defaultValue = Fields.Address.DEFAULT) Set<String> fields
    ) throws PapersException {
        return responseFactory.get(addressService.getAddressMap(offset, limit, fields));
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.PUT
    )
    public
    @ResponseBody Response<Integer>
    createAuthor(
            @RequestBody AddressView view
    ) throws PapersException {
        sessionUtils.authorized();
        sessionUtils.isUserWithRole(RolesEnum.admin,RolesEnum.moderator);
        return responseFactory.get(addressService.createAddress(view));
    }

    @RequestMapping(
            value = "/"
            , method = RequestMethod.POST)
    public
    @ResponseBody
    Response<Integer> save(
            @RequestBody AddressView view) throws PapersException {
        sessionUtils.authorized();
        sessionUtils.isUserWithRole(RolesEnum.admin,RolesEnum.moderator);
        return responseFactory.get(addressService.updateAddress(view));
    }
}
