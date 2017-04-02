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

    private Integer publisherId;

    private PublicationStatusEnum status;

    private List<Integer> authorsId;

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

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public PublicationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PublicationStatusEnum status) {
        this.status = status;
    }

    public List<Integer> getAuthorsId() {
        return authorsId;
    }

    public void setAuthorsId(List<Integer> authorsId) {
        this.authorsId = authorsId;
    }

    @Override
    public String toString() {
        return "PublicationView{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", type=" + type +
                ", link='" + link + '\'' +
                ", publisherId=" + publisherId +
                ", status=" + status +
                ", authorsId=" + authorsId +
                '}';
    }
}
