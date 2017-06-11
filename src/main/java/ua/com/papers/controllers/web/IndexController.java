package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publications.IPublicationService;

/**
 * Created by Andrii on 27.07.2016.
 */
@Controller
public class IndexController {

    @Autowired
    private IPublicationService publicationService;

    @Autowired
    private IAuthorService authorService;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String indexPage(Model model){
        int publications = 0;
        int authors = 0;

        try {
            publications = publicationService.countPublications(PUBLICATION_RESTRICT);
        } catch (WrongRestrictionException e) {
            e.printStackTrace();
        }

        try {
            authors = authorService.countAuthors(null);
        } catch (WrongRestrictionException e) {
            e.printStackTrace();
        }

        model.addAttribute("publication_count", publications);
        model.addAttribute("authors_count", authors);

        return "index/index";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/sign_up", method = RequestMethod.GET)
    public String signUp(){
        return "auth/sign_up";
    }


    private String PUBLICATION_RESTRICT = "{\"in_index\": true, \"status\": \"ACTIVE\"}";
}
