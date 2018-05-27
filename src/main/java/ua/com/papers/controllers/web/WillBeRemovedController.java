package ua.com.papers.controllers.web;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;
import ua.com.papers.services.recommendations.IRecommendationsService;
import ua.com.papers.services.redis.IRedisService;
import ua.com.papers.services.stop_words_dictionary.IStopWordsDictionaryService;

import java.util.HashMap;
import java.util.Map;

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
