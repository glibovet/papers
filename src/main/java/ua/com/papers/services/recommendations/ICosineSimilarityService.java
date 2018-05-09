package ua.com.papers.services.recommendations;

import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.temporary.Document;

import java.util.HashSet;
import java.util.List;

public interface ICosineSimilarityService {
    void processDocuments(List<Document> documents, HashSet<String> dictionary);
}
