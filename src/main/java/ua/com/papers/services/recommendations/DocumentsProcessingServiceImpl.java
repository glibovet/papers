package ua.com.papers.services.recommendations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.temporary.Document;
import ua.com.papers.services.publications.IPublicationService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentsProcessingServiceImpl implements IDocumentsProcessingService {

    @Autowired
    private ITextService textService;

    @Autowired
    private IPublicationService publicationService;

    public List<Document> prepareDocumentsCollection() {
        List<Document> list = new ArrayList<>();
        List<PublicationEntity> publications =  publicationService.getAllPublications();
        for (PublicationEntity publication : publications) {
            String fileLink = publication.getFileLink();
            String title = publication.getTitle();
            PDDocument pddDocument = null;
            try {
                pddDocument = PDDocument.load(new URL(fileLink));
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(pddDocument);
                List<String> words = this.textService.breakTextIntoUniGramsAndBiGrams(text);
                list.add(new Document(publication.getId(), text, words));
                pddDocument.close();
                break;
            } catch (IOException e) {
//                TODO: problem
//                http://www.ekmair.ukma.edu.ua/bitstream/handle/123456789/7951/Yeryemyeyev_Osnovni_rysy_liberal%27noyi.pdf is OK
//                http://www.ekmair.ukma.edu.ua/bitstream/handle/123456789/6896/Pavlova_Ky%60yivs%60ki_vijty%60.pdf is NOT OK
//                e.printStackTrace();
            }
        }

        return list;
    }

}
