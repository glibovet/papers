package ua.com.papers.pojo.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Andrii on 15.09.2016.
 */
@Entity
@Table(name="author")
public class AuthorEntity implements Serializable{
    private static final long serialVersionUID = 5288714347007989702L;
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="last_name")
    @Size(max = 75, message = "error.author.last_name.size")
    private String lastName;

    @Column(name="initials")
    @Size(max = 45, message = "error.author.initials.size")
    private String initials;

    @Column(name="as_it")
    @Size(max = 250, message = "error.author.as_it.size")
    private String original;

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.REFRESH,CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name="author_master_id")
    private AuthorMasterEntity master;

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

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public AuthorMasterEntity getMaster() {
        return master;
    }

    public void setMaster(AuthorMasterEntity master) {
        this.master = master;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorEntity that = (AuthorEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (initials != null ? !initials.equals(that.initials) : that.initials != null) return false;
        return original != null ? original.equals(that.original) : that.original == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (initials != null ? initials.hashCode() : 0);
        result = 31 * result + (original != null ? original.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuthorEntity{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", initials='" + initials + '\'' +
                ", original='" + original + '\'' +
                '}';
    }
}
