package ua.com.papers.services.search;

import ua.com.papers.pojo.entities.PublicationEntity;

/**
 * Created by Andrii on 29.09.2016.
 */
public interface ISearchService {

    void index(PublicationEntity publication);
}
