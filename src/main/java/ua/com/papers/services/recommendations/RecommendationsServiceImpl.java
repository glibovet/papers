package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.pojo.temporary.TfIdfItem;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;

import javax.print.Doc;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RecommendationsServiceImpl implements IRecommendationsService{

    @Autowired
    private IDocumentsProcessingService documentsProcessingService;

    @Autowired
    private ITfIdfService tfIdfService;

    @Autowired
    private ICosineSimilarityService cosineSimilarityService;

    @Autowired
    private IPublicationsCosineSimilarityService publicationsCosineSimilarityService;

    public void generate() {
        // 1. Delete previous Cosine similarity calculations
        publicationsCosineSimilarityService.deleteAll();

        // 2. Prepare documents collection: Create document with publication id, text and list of words.
        List<Document> documents = documentsProcessingService.prepareDocumentsCollection();

        // 3. Calculate tf-idf for every word in every document.
        this.tfIdfService.calculateTfIdfForDocuments(documents);

        // 4. Sort tf-idf list by value in every document
        for(Document document : documents) {
            Collections.sort(document.getTfIdfItems());
        }

        // 5. Create dictionary - dictionary can be created only after all key words for documents have been fined
        HashSet<String> dictionary = new HashSet<>();
        for(Document document : documents) {
            for (int i=0; i< 15; i++) {
                dictionary.add(document.getTfIdfItems().get(i).getWord());
            }
        }

        // 6. Calculate cosine similarity between all pairs of documents and save into db
        this.cosineSimilarityService.processDocuments(documents, dictionary);

    }
}
