package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ElasticSearchError;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.response.Response;
import ua.com.papers.pojo.response.ResponseFactory;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.search.ISearchService;
import ua.com.papers.services.utils.SessionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Andrii on 12.11.2016.
 */
@Controller
@RequestMapping(value = "/api/elastic")
public class ElasticSearchApiController {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private IElasticSearch elasticSearch;

    @Autowired
    private ISearchService searchService;

    @RequestMapping(
            value = "/index",
            method = RequestMethod.POST
    )
    public @ResponseBody Response<Boolean> createIndex() throws ForbiddenException, ElasticSearchError {
        return responseFactory.get(elasticSearch.createIndexIfNotExist());
    }

    @RequestMapping(
            value = "/index_all",
            method = RequestMethod.POST
    )
    public @ResponseBody Response<Boolean> indexAll() throws ForbiddenException, ElasticSearchError {
        return responseFactory.get(elasticSearch.indexAll());
    }

    @RequestMapping(
            value = "/index",
            method = RequestMethod.DELETE
    )
    public @ResponseBody Response<Boolean> deleteIndex() throws ForbiddenException, ElasticSearchError {
        return responseFactory.get(elasticSearch.indexDelete());
    }

    @RequestMapping(
            value = "/publication/{id}/index",
            method = RequestMethod.POST
    )
    public
    @ResponseBody
    Response<Boolean> indexPublication(
            @PathVariable("id") int id) throws NoSuchEntityException, ForbiddenException, ServiceErrorException, ValidationException, ElasticSearchError {
        return responseFactory.get(elasticSearch.indexPublication(id));
    }

    @RequestMapping(
            value = "/publication/search",
            method = RequestMethod.GET
    )
    public
    @ResponseBody
    Response<List<PublicationDTO>> searchPublication(@RequestParam("q") String query,
                                                        @RequestParam(value = "offset",  required = false, defaultValue = "0") int offset){
        return responseFactory.get(searchService.search(query, offset));
    }

}
