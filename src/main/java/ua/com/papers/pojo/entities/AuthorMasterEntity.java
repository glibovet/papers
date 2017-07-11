package ua.com.papers.pojo.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Andrii on 15.09.2016.
 */
@Entity
@Table(name="author_master")
public class AuthorMasterEntity implements Serializable{

    private static final long serialVersionUID = -1224775354037000481L;

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="last_name")
    @Size(max = 100, message = "error.author.master.last_name.size")
    private String lastName;

    @Column(name="initials")
    @Size(max = 15, message = "error.author.master.initials.size")
    private String initials;

    @OneToMany(mappedBy="master", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AuthorEntity> authors;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name="author_to_publication",
            joinColumns=@JoinColumn(name="author_master_id", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="publication_id", referencedColumnName="ID"))
    private Set<PublicationEntity> publications;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public Set<AuthorEntity> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorEntity> authors) {
        this.authors = authors;
    }

    public Set<PublicationEntity> getPublications() {
        return publications;
    }

    public void setPublications(Set<PublicationEntity> publications) {
        this.publications = publications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorMasterEntity that = (AuthorMasterEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        return initials != null ? initials.equals(that.initials) : that.initials == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (initials != null ? initials.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuthorMasterEntity{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", initials='" + initials + '\'' +
                ", authors=" + authors +
                '}';
    }

    public void addAuthor(AuthorEntity author){
        if(author == null)
            return;
        if (this.authors==null)
            authors = new HashSet<>();
        authors.add(author);
    }
}
