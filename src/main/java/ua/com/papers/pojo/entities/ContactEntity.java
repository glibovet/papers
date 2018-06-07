package ua.com.papers.pojo.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "contacts")
public class ContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "isAccepted")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isAccepted;

    @Column(name = "attachment")
    private String attachment;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_from")
    private UserEntity userFrom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_to")
    private UserEntity userTo;

    @Column(name="date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(UserEntity userFrom) {
        this.userFrom = userFrom;
    }

    public UserEntity getUserTo() {
        return userTo;
    }

    public void setUserTo(UserEntity userTo) {
        this.userTo = userTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactEntity that = (ContactEntity) o;
        //TODO all fields
        return id != that.id;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        //TODO all fields
        return result;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "id=" + id +
                ", isAccepted=" + isAccepted +
                ", attachment='" + attachment + '\'' +
                ", message='" + message + '\'' +
                ", userFrom=" + userFrom +
                ", userTo=" + userTo +
                '}';
    }
}
