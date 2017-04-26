package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.services.elastic.IElasticSearch;

import java.util.List;

/**
 * Created by mogo on 4/17/17.
 */
@Controller
public class SearchController {

    @Autowired
    private IElasticSearch elasticSearch;

    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public String indexPage(
            @RequestParam("q") String query,
            @RequestParam(value = "offset",  required = false, defaultValue = "0") int offset,
            Model model
    ){
        List<PublicationDTO> publications = elasticSearch.search(query, offset);
        model.addAttribute("publications", publications);
        model.addAttribute("query", query);
        if(offset != 0)
            model.addAttribute("offset", offset);
        return "index/search";
    }
}
