package ua.com.papers.services.search;

import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.PublicationEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Andrii on 29.09.2016.
 */
public interface ISearchService {

    void index(PublicationEntity publication);
    List<PublicationDTO> search(String query, int offset);
}
