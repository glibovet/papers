package ua.com.papers.pojo.entities;

import javax.persistence.*;

@Entity
@Table(name = "dictionary")
public class DictionaryEntity {

    @Id
    @Column(name = "word")
    private String word;

    public DictionaryEntity(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
