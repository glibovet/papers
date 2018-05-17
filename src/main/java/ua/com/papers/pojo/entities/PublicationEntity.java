package ua.com.papers.pojo.entities;

import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.enums.UploadStatus;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
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

    @Column(name = "upload_status")
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus = UploadStatus.PENDING;

    @Column(name="link")
    @Size(max = 500, message = "error.publication.link.size")
    private String link;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="publisher_id")
    private PublisherEntity publisher;

    @Column(name="in_index")
    private boolean inIndex;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private PublicationStatusEnum status;

    @Column(name="literature_parsed")
    private boolean literatureParsed;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE/*, CascadeType.PERSIST, CascadeType.REFRESH*/})
    @JoinTable(
            name="author_to_publication",
            joinColumns=@JoinColumn(name="publication_id", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="author_master_id", referencedColumnName="ID"))
    private Set<AuthorMasterEntity> authors;

    @Column(name="fileNameOriginal")
    private String fileNameOriginal;

    @Column(name = "file_link")
    private String fileLink;

    @Column(name = "content_length")
    private long contentLength;


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

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicationEntity)) return false;
        PublicationEntity that = (PublicationEntity) o;
        return inIndex == that.inIndex &&
                literatureParsed == that.literatureParsed &&
                contentLength == that.contentLength &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(annotation, that.annotation) &&
                type == that.type &&
                uploadStatus == that.uploadStatus &&
                Objects.equals(link, that.link) &&
                Objects.equals(publisher, that.publisher) &&
                status == that.status &&
                Objects.equals(authors, that.authors) &&
                Objects.equals(fileNameOriginal, that.fileNameOriginal) &&
                Objects.equals(fileLink, that.fileLink);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, annotation, type, uploadStatus, link, publisher, inIndex, status, literatureParsed, authors, fileNameOriginal, fileLink, contentLength);
    }

}
