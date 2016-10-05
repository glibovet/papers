package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.exceptions.service_error.AuthRequiredException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.storage.IStorageService;

import java.io.IOException;

/**
 * Created by Andrii on 05.10.2016.
 */
@Controller
@RequestMapping("/api/storage")
public class StorageApiController {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private IStorageService storageService;

    @RequestMapping(value = "/{id}/paper", method = RequestMethod.POST)
    public @ResponseBody
    Response<Boolean>
    uploadPaper(
            @PathVariable("id") int id,
            @RequestParam("file") MultipartFile file) throws PapersException, IOException {
        sessionUtils.userHasRole(RolesEnum.admin, RolesEnum.moderator);
        return responseFactory.get(storageService.uploadPaper(id, file));
    }


}
