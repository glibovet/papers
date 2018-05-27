package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;
import ua.com.papers.services.recommendations.IRecommendationsService;
import ua.com.papers.services.redis.IRedisService;
import ua.com.papers.services.utils.MapUtils;
import ua.com.papers.services.utils.SessionUtils;

import java.util.*;

@Controller
public class RecommendationsController {

    @Autowired
    private IRecommendationsService recommendationsService;

    @Autowired
    private IPublicationService publicationService;

   @Autowired
   private IPublicationsCosineSimilarityService publicationsCosineSimilarityService;

    @Autowired
    private IRedisService redisService;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private MapUtils mapUtils;

    @RequestMapping(value = "/recommendations/to-publication/{id}",
            method = RequestMethod.GET)
    public String showForSelectedPublicationPage(ModelMap model, @PathVariable Integer id) throws NoSuchEntityException {
        PublicationEntity publication = publicationService.getPublicationById(id);
        List<PublicationsCosineSimilarityEntity> recommendations = publicationsCosineSimilarityService.findSimilar(publication, new PageRequest(0, 5));

        model.addAttribute("publication", publication);
        model.addAttribute("recommendations", recommendations);

        return "recommendations/showForSelectedPublication";
    }

    @RequestMapping(value = "/recommendations/based-on-interactions",
            method = RequestMethod.GET)
    public String showBasedOnInteractionsPage(ModelMap model) {
        UserEntity user = this.sessionUtils.getCurrentUser();
        HashSet<PublicationEntity> recommendations = new HashSet<>();
        if(user != null) {
            Map<Integer, Double> hm = this.mapUtils.getSortedMap(this.redisService.getCTRMap(user.getId()), "DESC");
            recommendations = this.recommendationsService.prepareBasedOnInteractions(hm);
        }
        model.addAttribute("recommendations", recommendations);

        return "recommendations/showBasedOnInteractions";
    }

}
