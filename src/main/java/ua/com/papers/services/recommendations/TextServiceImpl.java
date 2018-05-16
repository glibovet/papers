package ua.com.papers.services.recommendations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.services.stemmer.IUkrainianStemmer;
import ua.com.papers.services.stop_words_dictionary.IStopWordsDictionaryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class TextServiceImpl implements ITextService{

    @Autowired
    private IUkrainianStemmer stemmer;

    @Autowired
    private IStopWordsDictionaryService stopWordsDictionaryService;

    /**
     * @param document
     * @return String
     * @throws IOException
     */
    private String getText(PDDocument document) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();

        return text;
    }

    /**
     * @param word
     * @return String
     */
    private  String normalizeString(String word) {
        return word.toLowerCase().replaceAll("[^a-zа-яіїєґ']+","");
    }

    private String getStemmed(String word) {
        this.stemmer.setCurrent(word);
        this.stemmer.stem();

        return this.stemmer.getCurrent();
    }

    /**
     *
     * @param text
     * @return List<String>
     */
    public List<String> breakTextIntoUniGramsAndBiGrams(String text) {
        // LinkedHashSet
        List<String> words = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(text);

        if(st.hasMoreTokens()) {
            String temp = this.getStemmed(this.normalizeString(st.nextToken()));
            while (st.hasMoreTokens()) {
                String word = this.getStemmed(this.normalizeString(st.nextToken()));
                if (temp.length() > 2 && !this.stopWordsDictionaryService.wordExistsInDictionary(temp)) {
                    words.add(temp);
                    if (word.length() > 2 && !this.stopWordsDictionaryService.wordExistsInDictionary(word)) {
                        words.add(temp + " " + word);
                    }
                }
                temp = word;
            }
        }

        return words;
    }

}
