package ua.com.papers.controllers.web;

import com.google.common.io.BaseEncoding;
import org.elasticsearch.common.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.redis.IRedisService;
import ua.com.papers.services.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

/**
 * Created by mogo on 4/17/17.
 */
@Controller
public class SearchController {

    @Autowired
    private IElasticSearch elasticSearch;

    @Autowired
    private IRedisService redisService;

    @Autowired
    private SessionUtils sessionUtils;

    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public String indexPage(
            @RequestParam("q") String query,
            @RequestParam(value = "offset",  required = false, defaultValue = "0") int offset,
            Model model,
            HttpServletRequest request
    ){
        List<PublicationDTO> publications = elasticSearch.search(query, offset);

        UserEntity user = this.sessionUtils.getCurrentUser();
        if(user != null) {
            redisService.registerShownPublications(user.getId(), publications);
        }

        model.addAttribute("publications", publications);
        model.addAttribute("query", query);
        if(offset != 0)
            model.addAttribute("offset", offset);
        return "index/search";
    }
}
