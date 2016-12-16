package ua.com.papers.services.elastic;

import ua.com.papers.exceptions.service_error.ElasticSearchError;
import ua.com.papers.exceptions.service_error.ForbiddenException;

/**
 * Created by Andrii on 12.11.2016.
 */
public interface IElasticSearch {
    Boolean createIndexIfNotExist() throws ForbiddenException, ElasticSearchError;
    Boolean indexExist() throws ElasticSearchError;
    Boolean indexDelete() throws ElasticSearchError, ForbiddenException;
}
