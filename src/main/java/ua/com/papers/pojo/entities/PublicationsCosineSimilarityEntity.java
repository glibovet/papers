package ua.com.papers.pojo.entities;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "publications_cosine_similarity")
public class PublicationsCosineSimilarityEntity implements Serializable{

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="value")
    private Double value;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="publication1_id")
    private PublicationEntity publication1;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="publication2_id")
    private PublicationEntity publication2;

    public PublicationsCosineSimilarityEntity(Double value, PublicationEntity publication1, PublicationEntity publication2) {
        this.value = value;
        this.publication1 = publication1;
        this.publication2 = publication2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public PublicationEntity getPublication1() {
        return publication1;
    }

    public void setPublication1(PublicationEntity publication1) {
        this.publication1 = publication1;
    }

    public PublicationEntity getPublication2() {
        return publication2;
    }

    public void setPublication2(PublicationEntity publication2) {
        this.publication2 = publication2;
    }
}
