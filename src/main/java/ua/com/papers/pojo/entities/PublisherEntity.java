package ua.com.papers.pojo.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Andrii on 15.09.2016.
 */
@Entity
@Table(name="publisher")
public class PublisherEntity implements Serializable{

    private static final long serialVersionUID = 2865521727705924574L;
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="title")
    @Size(max = 500, message = "error.publisher.title.size")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="URL")
    @Size(max = 200, message = "error.publisher.url.size")
    private String url;

    @Column(name="contacts")
    private String contacts;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="address_id")
    private AddressEntity address;

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

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublisherEntity that = (PublisherEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return contacts != null ? contacts.equals(that.contacts) : that.contacts == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (contacts != null ? contacts.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PublisherEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", contacts='" + contacts + '\'' +
                ", address=" + address +
                '}';
    }
}
