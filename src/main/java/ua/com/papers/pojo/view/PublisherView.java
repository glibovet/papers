package ua.com.papers.pojo.view;

import javax.validation.constraints.Size;

/**
 * Created by Andrii on 02.10.2016.
 */
public class PublisherView {

    private Integer id;
    @Size(max = 500, message = "error.publisher.title.size")
    private String title;
    private String description;
    @Size(max = 200, message = "error.publisher.url.size")
    private String url;
    private String contacts;
    private Integer address;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }
}
