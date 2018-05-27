package ua.com.papers.services.stop_words_dictionary;

import org.springframework.data.domain.Pageable;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.pojo.entities.StopWordsDictionaryEntity;

import java.util.HashSet;
import java.util.List;

public interface IStopWordsDictionaryService {
    List<StopWordsDictionaryEntity> getAllStopWordsList();
    boolean wordExistsInDictionary(String word);
    HashSet<String> getWords();
}
