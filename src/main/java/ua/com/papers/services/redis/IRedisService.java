package ua.com.papers.services.redis;

import java.util.HashMap;

public interface IRedisService {
    void updateKey(Integer userId, Integer publicationId, String action);
    HashMap<Integer, Double> getCTRMap(Integer userId);
}
