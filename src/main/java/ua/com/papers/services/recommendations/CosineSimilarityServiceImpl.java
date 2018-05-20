package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationsCosineSimilarityEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.services.publications_document_similarity.IPublicationsCosineSimilarityService;

import java.util.HashSet;
import java.util.List;

@Service
public class CosineSimilarityServiceImpl implements ICosineSimilarityService {

    @Autowired
    private IPublicationsCosineSimilarityService publicationsCosineSimilarityService;

    private double calculate(Document d1, Document d2, HashSet<String> dictionary) {
        double numerator = 0;
        double d1denominator = 0;
        double d2denominator = 0;
        for (String s : dictionary) {
            Double v1 = d1.findTfIdfValue(s);
            Double v2 = d2.findTfIdfValue(s);

            if (v1!=null && v2!=null) {
                numerator += v1*v2;
            }

            if (v1!=null) {
                d1denominator += v1*v1;
            }
            if (v2!=null) {
                d2denominator += v2*v2;
            }

        }
        return numerator / (Math.sqrt(d1denominator) * Math.sqrt(d2denominator));
    }

    private PublicationsCosineSimilarityEntity prepareEntity(Document d1, Document d2, HashSet<String> dictionary) {
        double similarityValue = this.calculate(d1, d2, dictionary);

        return new PublicationsCosineSimilarityEntity(similarityValue,d1.getPublication(),d2.getPublication());
    }

    public void processDocuments(List<Document> documents, HashSet<String> dictionary){
        for(int i = 0; i < documents.size(); i++) {
            for(int j = i+1; j< documents.size(); j++) {
                PublicationsCosineSimilarityEntity entity = this.prepareEntity(documents.get(i), documents.get(j), dictionary);
                if(!Double.isNaN(entity.getValue()) && entity.getValue() > 0.0) {
                    // save into db
                    publicationsCosineSimilarityService.save(entity);
                }
            }
        }
    }

}
