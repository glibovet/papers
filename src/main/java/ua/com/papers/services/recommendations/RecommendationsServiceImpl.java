package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.pojo.temporary.TfIdfItem;

import javax.print.Doc;
import java.util.*;

@Service
public class RecommendationsServiceImpl implements IRecommendationsService{

    @Autowired
    private IDocumentsProcessingService documentsProcessingService;

    @Autowired
    private ITfIdfService tfIdfService;

    @Autowired
    private ICosineSimilarityService cosineSimilarityServce;

    public void generate() {
        // 1. Prepare documents collection: Create document with publication id, text and list of words.
        List<Document> documents = documentsProcessingService.prepareDocumentsCollection();

        // 2. Calculate tf-idf for every word in every document.
        this.tfIdfService.calculateTfIdfForDocuments(documents);

        // 3. Sort tf-idf list by value in every document
        for(Document document : documents) {
            Collections.sort(document.getTfIdfItems());
        }

        // 4. Create dictionary - dictionary can be created only after all key words for documents have been fined
        HashSet<String> dictionary = new HashSet<>();
        for(Document document : documents) {
            for (int i=0; i< 20; i++) {
                dictionary.add(document.getTfIdfItems().get(i).getWord());
            }
        }

        // 5. Calculate cosine similarity between all pairs of documents and save into db
        this.cosineSimilarityServce.processDocuments(documents, dictionary);

    }
}
