package ua.com.papers.pojo.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Andrii on 28.09.2016.
 */
public class AuthorView {
    private Integer id;
    @Size(max = 75, message = "error.author.last_name.size")
    private String lastName;
    @Size(max = 45, message = "error.author.initials.size")
    private String initials;
    @Size(max = 250, message = "error.author.as_it.size")
    @NotNull
    private String original;
    private Integer masterId;

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

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }
}
