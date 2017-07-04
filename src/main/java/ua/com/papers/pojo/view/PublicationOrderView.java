package ua.com.papers.pojo.view;

import org.hibernate.validator.constraints.Email;
import ua.com.papers.pojo.enums.PublicationOrderStatusEnum;

import javax.validation.constraints.Size;

/**
 * Created by Andrii on 20.05.2017.
 */
public class PublicationOrderView {

    private Integer id;
    @Size(max = 50, message = "error.publication.email.size")
    @Email(message = "error.publication.email")
    private String email;
    private String reason;
    private String answer;
    private PublicationOrderStatusEnum status;
    private Integer publication_id;

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

    public Integer getPublication_id() {
        return publication_id;
    }

    public void setPublication_id(Integer publication_id) {
        this.publication_id = publication_id;
    }
}
