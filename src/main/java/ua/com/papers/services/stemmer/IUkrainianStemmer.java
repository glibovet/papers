package ua.com.papers.services.stemmer;

public interface IUkrainianStemmer {
    boolean stem();
    void setCurrent(String value);
    String getCurrent();
}
