package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.services.recommendations.IDocumentsProcessingService;
import ua.com.papers.services.recommendations.IRecommendationsService;

@Controller
public class WillBeRemovedController {

   @Autowired
   private IRecommendationsService recommendationsService;

    @RequestMapping(value = {"/will-be-removed"}, method = RequestMethod.GET)
    public String indexPage() {
        recommendationsService.generate();

        return null;
    }

}
