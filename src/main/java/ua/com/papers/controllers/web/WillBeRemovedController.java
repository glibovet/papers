package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.persistence.dao.repositories.PublicationsCosineSimilarityRepository;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;
import ua.com.papers.services.recommendations.IDocumentsProcessingService;
import ua.com.papers.services.recommendations.IRecommendationsService;

import java.util.List;

@Controller
public class WillBeRemovedController {

   @Autowired
   private IRecommendationsService recommendationsService;

   @Autowired
   private PublicationRepository publicationRepository;

   @Autowired
   private IPublicationsCosineSimilarityService publicationsCosineSimilarityService;

    @RequestMapping(value = {"/will-be-removed"}, method = RequestMethod.GET)
    public String indexPage() {

        PublicationEntity publication = publicationRepository.findOne(100);
        List<PublicationsCosineSimilarityEntity> list = publicationsCosineSimilarityService.findSimilar(publication, new PageRequest(0, 5));
        for (PublicationsCosineSimilarityEntity pcs : list) {
           System.out.println("ID="+ pcs.getId() + " " + pcs.getPublication1().getId() + " " + pcs.getPublication2().getId() + " = " + pcs.getValue());
        }
        //recommendationsService.generate();

        return null;
    }

}
