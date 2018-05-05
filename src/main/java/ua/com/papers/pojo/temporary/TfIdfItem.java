package ua.com.papers.pojo.temporary;

public class TfIdfItem implements Comparable<TfIdfItem> {
    private String word;
    private Double value;

    public TfIdfItem(String word, Double value) {
        this.word = word;
        this.value = value;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public int compareTo(TfIdfItem item) {
        return value.compareTo(item.getValue());
    }
}
