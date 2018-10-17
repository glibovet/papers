package ua.com.papers.services.recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.services.stemmer.IUkrainianStemmer;
import ua.com.papers.services.stop_words_dictionary.IStopWordsDictionaryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class TextServiceImpl implements ITextService{

    @Autowired
    private IUkrainianStemmer stemmer;

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
    public List<String> breakTextIntoUniGramsAndBiGrams(String text, HashSet<String> stopWords) {
        // LinkedHashSet
        List<String> words = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(text);

        if(st.hasMoreTokens()) {
            String temp = this.getStemmed(this.normalizeString(st.nextToken()));
            while (st.hasMoreTokens()) {
                try {
                    String word = this.getStemmed(this.normalizeString(st.nextToken()));
                    if (temp.length() > 2 && !stopWords.contains(word)) {
                        words.add(temp);
                        if (word.length() > 2 && !stopWords.contains(word)) {
                            words.add(temp + " " + word);
                        }
                    }
                    temp = word;
                }
//                TODO: some problem in stemmer
                catch(StringIndexOutOfBoundsException e) {
                }
            }
        }

        return words;
    }

//    /**
//     *
//     * @param text
//     * @return List<String>
//     */
//    public List<String> breakTextIntoTokens(String text, HashSet<String> stopWords) {
//        List<String> words = new ArrayList<String>();
//        StringTokenizer st = new StringTokenizer(text);
//        while (st.hasMoreTokens()) {
//            try {
//                String word = this.getStemmed(this.normalizeString(st.nextToken()));
//                if (word.length() > 2 && !stopWords.contains(word)) {
//                    words.add(word);
//                }
//            }
//            // Unexpected error happens in stemmer
//            catch(StringIndexOutOfBoundsException e){
//
//            }
//        }
//
//        return words;
//    }

}