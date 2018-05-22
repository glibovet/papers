package ua.com.papers.services.redis;

import ua.com.papers.pojo.dto.search.PublicationDTO;

import java.util.Map;
import java.util.List;

public interface IRedisService {
    void updateKey(Integer userId, Integer publicationId, String action);
    Map<Integer, Double> getCTRMap(Integer userId);
    void registerShownPublications(Integer userId, List<PublicationDTO> publications);
}
