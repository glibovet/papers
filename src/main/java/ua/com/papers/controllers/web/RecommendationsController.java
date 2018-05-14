package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;
import ua.com.papers.services.recommendations.IRecommendationsService;

import java.util.List;

@Controller
public class RecommendationsController {

    @Autowired
    private IPublicationService publicationService;

   @Autowired
   private IPublicationsCosineSimilarityService publicationsCosineSimilarityService;

    @RequestMapping(value = "/recommendations/to-publication/{id}", method = RequestMethod.GET)
    public String showPage(ModelMap model, @PathVariable Integer id) throws NoSuchEntityException {
        PublicationEntity publication = publicationService.getPublicationById(id);
        List<PublicationsCosineSimilarityEntity> recommendations = publicationsCosineSimilarityService.findSimilar(publication, new PageRequest(0, 5));

        model.addAttribute("publication", publication);
        model.addAttribute("recommendations", recommendations);

        return "recommendations/show";
    }

}
