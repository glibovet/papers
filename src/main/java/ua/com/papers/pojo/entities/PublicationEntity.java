package ua.com.papers.pojo.entities;

import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Andrii on 15.09.2016.
 */
@Entity
@Table(name = "publication")
public class PublicationEntity implements Serializable {

    public PublicationEntity(){
        this.literatureParsed = false;
        this.inIndex = false;
        this.status = PublicationStatusEnum.IN_PROCESS;
    }

    private static final long serialVersionUID = 3943941275239417204L;
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="title")
    @Size(max = 500, message = "error.publication.title.size")
    private String title;

    @Column(name="annotation")
    private String annotation;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private PublicationTypeEnum type;

    @Column(name="link")
    @Size(max = 500, message = "error.publication.link.size")
    private String link;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="publisher_id")
    private PublisherEntity publisher;

    @Column(name="in_index")
    private boolean inIndex;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private PublicationStatusEnum status;

    @Column(name="literature_parsed")
    private boolean literatureParsed;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name="author_to_publication",
            joinColumns=@JoinColumn(name="publication_id", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="author_master_id", referencedColumnName="ID"))
    private Set<AuthorMasterEntity> authors;

    @Column(name="fileNameOriginal")
    private String fileNameOriginal;

    @Column(name = "file_link")
    private String fileLink;

    public String getFileNameOriginal() {
        return fileNameOriginal;
    }

    public void setFileNameOriginal(String fileNameOriginal) {
        this.fileNameOriginal = fileNameOriginal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public PublicationTypeEnum getType() {
        return type;
    }

    public void setType(PublicationTypeEnum type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public PublisherEntity getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherEntity publisher) {
        this.publisher = publisher;
    }

    public boolean isInIndex() {
        return inIndex;
    }

    public void setInIndex(boolean inIndex) {
        this.inIndex = inIndex;
    }

    public PublicationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PublicationStatusEnum status) {
        this.status = status;
    }

    public boolean isLiteratureParsed() {
        return literatureParsed;
    }

    public void setLiteratureParsed(boolean literatureParsed) {
        this.literatureParsed = literatureParsed;
    }

    public Set<AuthorMasterEntity> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorMasterEntity> authors) {
        this.authors = authors;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public void addAuthor(AuthorMasterEntity author) {
        if (this.authors == null)
            this.authors = new HashSet<>();

        this.authors.add(author);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicationEntity that = (PublicationEntity) o;

        if (inIndex != that.inIndex) return false;
        if (literatureParsed != that.literatureParsed) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (annotation != null ? !annotation.equals(that.annotation) : that.annotation != null) return false;
        if (type != that.type) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (publisher != null ? !publisher.equals(that.publisher) : that.publisher != null) return false;
        return status == that.status;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (annotation != null ? annotation.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        result = 31 * result + (inIndex ? 1 : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (literatureParsed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PublicationEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", type=" + type +
                ", link='" + link + '\'' +
                ", publisher=" + publisher +
                ", inIndex=" + inIndex +
                ", status=" + status +
                ", literatureParsed=" + literatureParsed +
                '}';
    }
}
