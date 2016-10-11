package ua.com.papers.pojo.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Andrii on 28.09.2016.
 */
public class AuthorMasterView {
    private Integer id;
    @Size(max = 100, message = "error.author.master.last_name.size")
    @NotNull
    private String last_name;
    @Size(max = 15, message = "error.author.master.initials.size")
    @NotNull
    private String initials;
    private List<Integer> authorsIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_ame) {
        this.last_name = last_ame;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public List<Integer> getAuthorsIds() {
        return authorsIds;
    }

    public void setAuthorsIds(List<Integer> authorsIds) {
        this.authorsIds = authorsIds;
    }
}
