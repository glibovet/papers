package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.com.papers.convertors.Fields;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.pojo.temporary.TfIdfItem;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;

import javax.print.Doc;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

    @Autowired
    private IPublicationService publicationService;

    public void generate() {
        // 1. Prepare documents collection: Create document with publication id, text and list of words.
        List<Document> documents = documentsProcessingService.prepareDocumentsCollection();

        // 2. Calculate tf-idf for every word in every document.
        this.tfIdfService.calculateTfIdfForDocuments(documents);

        // 3. Sort tf-idf list by value in desc order in every document
        for(Document document : documents) {
            Collections.sort(document.getTfIdfItems());
            Collections.reverse(document.getTfIdfItems());
        }

        // 4. Create dictionary - dictionary can be created only after all key words for documents have been fined
        HashSet<String> dictionary = new HashSet<>();
        for(Document document : documents) {
            int numberOfItems = 15;
            if(document.getTfIdfItems().size() < 15) {
                numberOfItems = document.getTfIdfItems().size();
            }
            if(numberOfItems > 0) {
                for (int i=0; i< numberOfItems; i++) {
                    dictionary.add(document.getTfIdfItems().get(i).getWord());
                }
            }
        }

        // 5. Delete previous Cosine similarity calculations
        publicationsCosineSimilarityService.deleteAll();

        // 6. Calculate cosine similarity between all pairs of documents and save into db
        this.cosineSimilarityService.processDocuments(documents, dictionary);
    }

    public HashSet<PublicationEntity> prepareBasedOnInteractions(Map<Integer, Double> hm) {
        HashSet<PublicationEntity> results = new HashSet<>();
        int i = 0;
        // Get 4 publication with highest CTR value and get 3 recommendations for each publications.
        // Duplicates will be ignored.
        for (Map.Entry<Integer, Double> entry : hm.entrySet()) {
            if (i == 4) break;
            Integer publicationId = entry.getKey();
            try {
                PublicationEntity publication = publicationService.getPublicationById(publicationId);
                List<PublicationsCosineSimilarityEntity> pcs = publicationsCosineSimilarityService.findSimilar(publication, new PageRequest(0, 3));
                for(PublicationsCosineSimilarityEntity item : pcs) {
                    if(item.getPublication1().getId().equals(publicationId)) {
                        results.add(item.getPublication2());
                    } else {
                        results.add(item.getPublication1());
                    }
                }
            } catch(NoSuchEntityException e) {
            }
            i++;
        }

        return results;
    }
}
