package ua.com.papers.pojo.view;

import java.util.Date;

public class MessageView {

    private Integer messageId;
    private Integer userId;
    private String userName;
    private String userLastName;
    private Integer chatId;
    private String text;
    private String date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
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
