package ua.com.papers.services.stop_words_dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.persistence.dao.repositories.PublicationsCosineSimilarityRepository;
import ua.com.papers.persistence.dao.repositories.StopWordsDictionaryRepository;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.pojo.entities.StopWordsDictionaryEntity;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

@Service
public class StopWordsDictionaryServiceImpl implements IStopWordsDictionaryService {

    @Autowired
    private StopWordsDictionaryRepository stopWordsDictionaryRepository;

    @Override
    @Transactional
    public List<StopWordsDictionaryEntity> getAllStopWordsList() {
        List<StopWordsDictionaryEntity> list = stopWordsDictionaryRepository.findAll();
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public boolean wordExistsInDictionary(String word) {
        List<StopWordsDictionaryEntity> list = stopWordsDictionaryRepository.getDictionaryItemsByWord(word);
        return list.size() != 0;
    }

    public HashSet<String> getWords() {
        HashSet<String> set =
                new HashSet<String>();
        List<StopWordsDictionaryEntity> list = this.getAllStopWordsList();
        for(StopWordsDictionaryEntity entity : list) {
            set.add(entity.getWord());
        }

        return set;
    }

}
