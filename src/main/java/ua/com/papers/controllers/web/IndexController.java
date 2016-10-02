package ua.com.papers.controllers.web;

import com.dropbox.core.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.pojo.storage.FileData;
import ua.com.papers.pojo.storage.FileItem;
import ua.com.papers.storage.IStorage;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Created by Andrii on 27.07.2016.
 */
@Controller
public class IndexController {

    @Autowired
    private IStorage storage;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String indexPage(Model model, Principal principal){
        return "index/index";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/sign_up", method = RequestMethod.GET)
    public String signUp(){
        return "auth/sign_up";
    }
}
