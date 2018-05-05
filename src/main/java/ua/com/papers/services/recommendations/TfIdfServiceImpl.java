package ua.com.papers.services.recommendations;

import org.springframework.stereotype.Service;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.pojo.temporary.TfIdfItem;

import java.util.Collections;
import java.util.List;

@Service
public class TfIdfServiceImpl implements ITfIdfService {

    private Double calculateTF(List<String> set, String word) {
        double n = Collections.frequency(set, word);

        return n / set.size();
    }

    private Double calculateIDF(List<Document> documents, String word) {
        double n = 0;
        for (Document document : documents) {
            List<String> list = document.getWords();
            if(list.contains(word)) {
                n++;
            }
        }

        return Math.log(documents.size() / n);
    }

    private Double calculateTfIdfForWord(List documents, List<String> words,  String word) {
        double tf = this.calculateTF(words, word);
        double idf = this.calculateIDF(documents, word);

        return tf * idf;
    }

    public void calculateTfIdfForDocuments(List<Document> documents) {
        for (Document document : documents) {
            List<String> words = document.getWords();
            for (String s : words) {
                document.addTfIdfItem(new TfIdfItem(s, this.calculateTfIdfForWord(documents, words, s)));
            }
        }

    }

}
