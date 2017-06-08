package ua.com.papers.pojo.entities;

import org.hibernate.validator.constraints.Email;
import ua.com.papers.pojo.enums.PublicationOrderStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andrii on 20.05.2017.
 */
@Entity
@Table(name = "publication_order")
public class PublicationOrderEntity implements Serializable{

    private static final long serialVersionUID = -7211436562946255333L;
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="email")
    @Size(max = 50, message = "error.publication.order.email.size")
    @Email(message = "error.publication.order.email")
    private String email;

    @Column(name="reason")
    @NotNull
    private String reason;

    @Column(name="answer")
    private String answer;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private PublicationOrderStatusEnum status;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="publication_id")
    private PublicationEntity publication;

    @Column(name="date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @PrePersist
    protected void onCreate() {
        dateCreated = new Date();
        status = PublicationOrderStatusEnum.NEW;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public PublicationOrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PublicationOrderStatusEnum status) {
        this.status = status;
    }

    public PublicationEntity getPublication() {
        return publication;
    }

    public void setPublication(PublicationEntity publication) {
        this.publication = publication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicationOrderEntity that = (PublicationOrderEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (answer != null ? !answer.equals(that.answer) : that.answer != null) return false;
        if (status != that.status) return false;
        return publication != null ? publication.equals(that.publication) : that.publication == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (publication != null ? publication.hashCode() : 0);
        return result;
    }
}
