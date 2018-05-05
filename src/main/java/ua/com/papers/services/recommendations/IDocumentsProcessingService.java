package ua.com.papers.services.recommendations;

import ua.com.papers.pojo.temporary.Document;

import java.util.List;

public interface IDocumentsProcessingService {
    List<Document> prepareDocumentsCollection();
}
