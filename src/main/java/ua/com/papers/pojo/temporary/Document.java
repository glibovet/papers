package ua.com.papers.pojo.temporary;

import java.util.ArrayList;
import java.util.List;

public class Document {

    private int id;
    private String text;
    private List<String> words;
    private List<TfIdfItem> tfIdfItems;

    public Document(int id, String text, List<String> words) {
        this.id = id;
        this.text = text;
        this.words = words;
        this.tfIdfItems = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void addWord(String word) {
        this.words.add(word);
    }

    public List<TfIdfItem> getTfIdfItems() {
        return tfIdfItems;
    }

    public void addTfIdfItems(TfIdfItem item) {
        this.tfIdfItems.add(item);
    }
}
