package ua.com.papers.services.elastic;

import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ElasticSearchError;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.dto.search.PublicationDTO;

import java.util.List;

/**
 * Created by Andrii on 12.11.2016.
 */
public interface IElasticSearch {
    Boolean createIndexIfNotExist() throws ForbiddenException, ElasticSearchError;
    Boolean indexExist() throws ElasticSearchError;
    Boolean indexDelete() throws ElasticSearchError, ForbiddenException, NoSuchEntityException;

    Boolean indexPublication(int id) throws ForbiddenException, NoSuchEntityException, ServiceErrorException, ValidationException, ElasticSearchError;

    boolean indexAll() throws ForbiddenException, ElasticSearchError;

    List<PublicationDTO> search(String query, int offset);
}
