package ua.com.papers.pojo.entities;

import javax.persistence.*;

@Entity
@Table(name = "stop_words_dictionary")
public class StopWordsDictionaryEntity {

    @Id
    @Column(name = "word")
    private String word;


    public StopWordsDictionaryEntity() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
