package ua.com.papers.pojo.view;

import java.util.Date;

public class MessageView {

    private Integer userId;
    private Integer chatId;
    private String text;
    private Date date;
    private String attachment;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "MessageView{" +
                "userId=" + userId +
                ", chatId=" + chatId +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", attachment='" + attachment + '\'' +
                '}';
    }
}
