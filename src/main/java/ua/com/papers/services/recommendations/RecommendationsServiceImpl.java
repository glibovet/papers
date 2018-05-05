package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.temporary.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RecommendationsServiceImpl implements IRecommendationsService{

    @Autowired
    private IDocumentsProcessingService documentsProcessingService;

    @Autowired
    private ITfIdfService tfIdfService;

    public void generate() {
        // 1. Prepare documents collection: Create document with publication id, text and list of words.
        List<Document> documents = documentsProcessingService.prepareDocumentsCollection();

        // 2. Calculate tf-idf for every word in every document.
        this.tfIdfService.calculateTfIdfForDocuments(documents);

        // 3. Sort tf-idf list by value in every document
        for(Document document : documents) {
            Collections.sort(document.getTfIdfItems());
        }


    }
}
