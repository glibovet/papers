package ua.com.papers.services.recommendations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.convertors.Fields;
import ua.com.papers.persistence.dao.repositories.PublicationRepository;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.stop_words_dictionary.IStopWordsDictionaryService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jdk.nashorn.internal.objects.NativeString.trim;

@Service
public class DocumentsProcessingServiceImpl implements IDocumentsProcessingService {

    @Autowired
    private ITextService textService;

   @Autowired
   private IStopWordsDictionaryService stopWordsDictionaryService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private IElasticSearch elasticSearch;

    //  files from elasticsearch index
    public List<Document> prepareDocumentsCollection() {
        List<Document> list = new ArrayList<>();
        List<PublicationDTO> publications = elasticSearch.getAllPublications();
        HashSet<String> stopWords = stopWordsDictionaryService.getWords();
        for(PublicationDTO item : publications) {
            Integer publicationId = item.getId();
            PublicationEntity publication = publicationRepository.findOne(publicationId);
            if (publication != null) {
                String text = item.getBody();
                List<String> words = this.textService.breakTextIntoTokens(text, stopWords);
                HashSet<String> uniqueWords = new HashSet<>();
                uniqueWords.addAll(words);
                list.add(new Document(publication, text, words, uniqueWords));
            }
        }

        return list;
    }

////  files from folder
//    public List<Document> prepareDocumentsCollection() {
//        List<Document> list = new ArrayList<>();
////        C:\dev\apache-tomcat-8.5.30\papers\publications
//        File dir = new File("C:/dev/apache-tomcat-8.5.30/papers/publications");
//        File[] files = dir.listFiles();
//
//        int i=0;
//        for (File child : files) {
//            String publicationId = child.getName();
//            PublicationEntity publication = publicationRepository.findOne(Integer.parseInt(publicationId));
//            String filePath = "C:/dev/apache-tomcat-8.5.30/papers/publications/" + (trim(publicationId) + "/" +trim(publicationId) + ".pdf");
//            try {
//                File file = new File(filePath);
//                PDDocument pddDocument = PDDocument.load(file);
//                PDFTextStripper pdfStripper = new PDFTextStripper();
//                if (publication != null) {
//                    String text = pdfStripper.getText(pddDocument);
//                    List<String> words = this.textService.breakTextIntoTokens(text);
//                    list.add(new Document(publication, text, words));
//                    pddDocument.close();
//                }
//            } catch (IOException e) {
//            }
//        }
//
//        return list;
//    }

//    files from web
//    public List<Document> prepareDocumentsCollection() {
//        List<Document> list = new ArrayList<>();
//        List<PublicationEntity> publications =  publicationService.getAllPublications();
//        int i=0;
//        for (PublicationEntity publication : publications) {
//            String fileLink = publication.getFileLink();
//            String title = publication.getTitle();
//            PDDocument pddDocument = null;
//            try {
//                pddDocument = PDDocument.load(new URL(fileLink));
//                PDFTextStripper pdfStripper = new PDFTextStripper();
////                String text = pdfStripper.getText(pddDocument);
////                List<String> words = this.textService.breakTextIntoUniGramsAndBiGrams(text);
////                list.add(new Document(publication, text, words));
//                pddDocument.close();
//                i++;
//            } catch (IOException e) {
////                TODO: problem
////                http://www.ekmair.ukma.edu.ua/bitstream/handle/123456789/7951/Yeryemyeyev_Osnovni_rysy_liberal%27noyi.pdf is OK
////                http://www.ekmair.ukma.edu.ua/bitstream/handle/123456789/6896/Pavlova_Ky%60yivs%60ki_vijty%60.pdf is NOT OK
////                e.printStackTrace();
//            }
//        }
//        System.out.println(i);
//        return list;
//    }

}
