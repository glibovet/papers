package ua.com.papers.pojo.view;

import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Andrii on 27.09.2016.
 */
public class PublicationView {

    private Integer id;

    @Size(max = 500, message = "error.publication.title.size")
    private String title;

    private String annotation;

    private PublicationTypeEnum type;

    @Size(max = 500, message = "error.publication.link.size")
    private String link;

    private Integer publisher_id;

    private PublicationStatusEnum status;

    private List<Integer> authors_id;

    private String file_link;

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

    public Integer getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(Integer publisher_id) {
        this.publisher_id = publisher_id;
    }

    public PublicationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PublicationStatusEnum status) {
        this.status = status;
    }

    public List<Integer> getAuthors_id() {
        return authors_id;
    }

    public void setAuthors_id(List<Integer> authors_id) {
        this.authors_id = authors_id;
    }

    public String getFile_link() {
        return file_link;
    }

    public void setFile_link(String file_link) {
        this.file_link = file_link;
    }
}
