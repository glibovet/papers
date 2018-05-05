package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.temporary.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationsServiceImpl implements IRecommendationsService{

    @Autowired
    private IDocumentsProcessingService documentsProcessingService;

    public void generate() {
        // 1. Prepare documents collection: Create document with publication id, text and list of words.
        List<Document> documents = documentsProcessingService.prepareDocumentsCollection();
    }
}
