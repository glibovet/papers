package ua.com.papers.controllers.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.criteria.impl.PublicationCriteria;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publications.IPublicationService;

import java.util.*;

/**
 * Created by Oleh on 11.07.2017.
 */
@Controller
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private IAuthorService authorService;
    @Autowired
    private IPublicationService publicationService;

    @RequestMapping(value = "/{id}/view", method = RequestMethod.GET)
    public String viewAuthor(
            @PathVariable("id") int id,
            Model model
    ) {
        try {
            Map<String, Object> author = authorService.getAuthorMasterMapId(id, AUTHOR_FIELDS);

            Collection<Integer> publicationsId = (Collection<Integer>)author.get("publications_id");
            if (publicationsId != null && !publicationsId.isEmpty()) {
                try {
                    PublicationCriteria criteria = new PublicationCriteria()
                            .ids(publicationsId);

                    List<Map<String, Object>> publications = publicationService.getPublicationsMap(
                            PUBLICATION_FIELDS, criteria
                    );

                    author.put("publications", publications);
                } catch (NoSuchEntityException e) { }
            }

            model.addAttribute("author", author);
        } catch (NoSuchEntityException e) {
            // return to 404
        }

        return "author/view";
    }

    static final Set<String> AUTHOR_FIELDS = new HashSet<>(Arrays.asList("id", "last_name", "initials", "publications_id"));
    static final Set<String> PUBLICATION_FIELDS = new HashSet<>(Arrays.asList("id", "title", "annotation", "link"));
}
