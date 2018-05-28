package ua.com.papers.pojo.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "message")
public class MessageEntity implements Comparable{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="chat_id", nullable = false)
    private ChatEntity chat;

    @Column(name="text")
    private String text;

    @Column(name="date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "attachment")
    private String attachment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ChatEntity getChat() {
        return chat;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", user=" + user +
                ", chat=" + chat +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", attachment='" + attachment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageEntity that = (MessageEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }

    @Override
    public int compareTo(Object o) {
        if(o == null) return 1;
        MessageEntity that = (MessageEntity) o;
        if(date == null) return -1;
        return date.compareTo(that.getDate());
    }
}
