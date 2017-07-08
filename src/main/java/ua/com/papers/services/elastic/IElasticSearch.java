package ua.com.papers.services.elastic;

import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.not_found.PublicationWithoutFileException;
import ua.com.papers.exceptions.service_error.ElasticSearchException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.dto.search.PublicationDTO;

import java.util.List;

/**
 * Created by Andrii on 12.11.2016.
 */
public interface IElasticSearch {
    Boolean createIndexIfNotExist() throws ForbiddenException, ElasticSearchException;
    Boolean indexExist() throws ElasticSearchException;
    Boolean indexDelete() throws ElasticSearchException, ForbiddenException, NoSuchEntityException;

    Boolean indexPublication(int id) throws ForbiddenException, NoSuchEntityException, ServiceErrorException, ValidationException, ElasticSearchException, PublicationWithoutFileException;

    boolean indexAll() throws ForbiddenException, ElasticSearchException, PublicationWithoutFileException;

    List<PublicationDTO> search(String query, int offset);
}
