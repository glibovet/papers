package ua.com.papers.pojo.temporary;

import ua.com.papers.pojo.entities.PublicationEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Document {

    private PublicationEntity publication;
    private String text;
    private List<String> words;
    // need this for calculationg IDF: cotains() operation works for hashset much quicker
    private HashSet<String> uniqueWords;
    private List<TfIdfItem> tfIdfItems;

    public Document(PublicationEntity publication, String text, List<String> words, HashSet<String> uniqueWords) {
        this.publication = publication;
        this.text = text;
        this.words = words;
        this.uniqueWords = uniqueWords;
        this.tfIdfItems = new ArrayList<>();
    }

    public PublicationEntity getPublication() {
        return publication;
    }

    public void setPublication(PublicationEntity publication) {
        this.publication = publication;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getWords() {
        return words;
    }

    public HashSet<String> getUniqueWords() {
        return uniqueWords;
    }

    public List<TfIdfItem> getTfIdfItems() {
        return tfIdfItems;
    }

    public void addTfIdfItem(TfIdfItem item) {
        this.tfIdfItems.add(item);
    }

    public Double findTfIdfValue(String word) {
//      I need subList of 15 elements because dictionary contains 15 words from every document,
//      but this.getTfIdfItems() contains all words.
        int numberOfWords = 15;
        if(this.words.size() < 15) {
            numberOfWords = this.words.size();
        }
        for(TfIdfItem item : this.getTfIdfItems().subList(0, numberOfWords)) {
            if(item.getWord().equals(word)) {
                return item.getValue();
            }
        }
        return 0.0;
    }

}
