package ua.com.papers.services.recommendations;

import ua.com.papers.pojo.entities.PublicationEntity;

import java.util.HashSet;
import java.util.Map;

public interface IRecommendationsService {
    void generate();
    HashSet<PublicationEntity> prepareBasedOnInteractions(Map<Integer, Double> hm);
}
