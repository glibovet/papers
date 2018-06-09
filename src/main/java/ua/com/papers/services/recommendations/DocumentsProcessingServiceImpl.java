package ua.com.papers.services.recommendations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.convertors.Fields;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.stop_words_dictionary.IStopWordsDictionaryService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jdk.nashorn.internal.objects.NativeString.trim;

@Service
public class DocumentsProcessingServiceImpl implements IDocumentsProcessingService {

    @Autowired
    private ITextService textService;

   @Autowired
   private IStopWordsDictionaryService stopWordsDictionaryService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private IElasticSearch elasticSearch;

    //  files from elasticsearch index
    public List<Document> prepareDocumentsCollection() {
        List<Document> list = new ArrayList<>();
        List<PublicationDTO> publications = elasticSearch.getAllPublications();
        HashSet<String> stopWords = stopWordsDictionaryService.getWords();
        for(PublicationDTO item : publications) {
            if((item.getLink().substring(0,21)).equals("http://nz.ukma.edu.ua")) {
                Integer publicationId = item.getId();
                PublicationEntity publication = publicationRepository.findOne(publicationId);
                if (publication != null) {
                    String text = item.getBody();
//                List<String> words = this.textService.breakTextIntoTokens(text, stopWords);
                    List<String> words = this.textService.breakTextIntoUniGramsAndBiGrams(text, stopWords);
                    HashSet<String> uniqueWords = new HashSet<>();
                    uniqueWords.addAll(words);
                    list.add(new Document(publication, text, words, uniqueWords));
                }
            }
        }

        return list;
    }

}
