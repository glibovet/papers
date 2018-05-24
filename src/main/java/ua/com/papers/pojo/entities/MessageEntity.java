package ua.com.papers.pojo.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message")
public class MessageEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity authorUser;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="chat_id", nullable = false)
    private ChatEntity chat;

    @Column(name="text")
    private String annotation;

    @Column(name="date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserEntity getAuthorUser() {
        return authorUser;
    }

    public void setAuthorUser(UserEntity authorUser) {
        this.authorUser = authorUser;
    }

    public ChatEntity getChat() {
        return chat;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
