package ua.com.papers.pojo.dto.search;

import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.enums.PublicationTypeEnum;

/**
 * Created by mogo on 1/13/17.
 */
public class PublicationDTO {

    private Integer id;
    private String title;
    private String authors;
    private String annotation;
    private String body;
    private String publisher;
    private String link;
    private PublicationTypeEnum type;

    public PublicationDTO() {}

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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    @Override
    public String toString() {
        return "PublicationDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors='" + authors + '\'' +
                ", annotation='" + annotation + '\'' +
                ", publisher='" + publisher + '\'' +
                ", link='" + link + '\'' +
                ", type=" + type +
                '}';
    }


}
