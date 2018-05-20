package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.pojo.temporary.TfIdfItem;
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

    public void generate() {
        try {
            FileWriter fw = new FileWriter("C:/dev/logs.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            long startTime = System.currentTimeMillis();
            // 1. Delete previous Cosine similarity calculations
            publicationsCosineSimilarityService.deleteAll();
            long estimatedTime = System.currentTimeMillis() - startTime;
            //System.out.println("1. DELETE ALL FROM DB TAKES: " + estimatedTime);
            bw.write("1. DELETE ALL FROM DB TAKES: " + estimatedTime + "\n");

            startTime = System.currentTimeMillis();
            // 2. Prepare documents collection: Create document with publication id, text and list of words.
            List<Document> documents = documentsProcessingService.prepareDocumentsCollection();
            estimatedTime = System.currentTimeMillis() - startTime;
            //System.out.println("2. PROCESSING DOCUMENTS TAKES: " + estimatedTime);
            bw.write("2. PROCESSING DOCUMENTS TAKES: " + estimatedTime + "\\r\\n");

            startTime = System.currentTimeMillis();
            // 3. Calculate tf-idf for every word in every document.
            this.tfIdfService.calculateTfIdfForDocuments(documents);
            estimatedTime = System.currentTimeMillis() - startTime;
            //System.out.println("3. CALCULATING TF-IDF TAKES: " + estimatedTime);
            bw.write("3. CALCULATING TF-IDF TAKES: " + estimatedTime + "\\r\\n");

            startTime = System.currentTimeMillis();
            // 4. Sort tf-idf list by value in every document
            for(Document document : documents) {
                Collections.sort(document.getTfIdfItems());
            }
            estimatedTime = System.currentTimeMillis() - startTime;
//            System.out.println("4. SORTING TF-IDF COLLECTION TAKES: " + estimatedTime);
            bw.write("4. SORTING TF-IDF COLLECTION TAKES: " + estimatedTime + "\\r\\n");

            startTime = System.currentTimeMillis();
            // 5. Create dictionary - dictionary can be created only after all key words for documents have been fined
            HashSet<String> dictionary = new HashSet<>();
            for(Document document : documents) {
                for (int i=0; i< 15; i++) {
                    dictionary.add(document.getTfIdfItems().get(i).getWord());
                }
            }
            estimatedTime = System.currentTimeMillis() - startTime;
//            System.out.println("5. CREATING DICTIONARY TAKES: " + estimatedTime);
            bw.write("5. CREATING DICTIONARY TAKES: " + estimatedTime + "\\r\\n");

            startTime = System.currentTimeMillis();
            // 6. Calculate cosine similarity between all pairs of documents and save into db
            this.cosineSimilarityService.processDocuments(documents, dictionary);
            estimatedTime = System.currentTimeMillis() - startTime;
//            System.out.println("5. CALCULATING CS TAKES: " + estimatedTime);
            bw.write("5. CALCULATING CS TAKES: " + estimatedTime + "\\r\\n");

            bw.close();
            fw.close();
        } catch(IOException e) {
        }


    }
}
