package ua.com.papers.pojo.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Andrii on 28.09.2016.
 */
public class AuthorView {
    private Integer id;
    @Size(max = 75, message = "error.author.last_name.size")
    private String last_name;
    @Size(max = 45, message = "error.author.initials.size")
    private String initials;
    @Size(max = 250, message = "error.author.as_it.size")
    @NotNull
    private String original;
    private Integer master_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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

    public Integer getMaster_id() {
        return master_id;
    }

    public void setMaster_id(Integer master_id) {
        this.master_id = master_id;
    }
}
