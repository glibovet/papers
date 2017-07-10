package ua.com.papers.pojo.dto.search;

import java.util.List;
import java.util.Set;

/**
 * Created by Andrii on 10.07.2017.
 */
public class AuthorDTO {
    private Integer id;
    private String name;
    private String initials;
    private int publicationCount;
    private Set<Integer> publicationIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public int getPublicationCount() {
        return publicationCount;
    }

    public void setPublicationCount(int publicationCount) {
        this.publicationCount = publicationCount;
    }

    public Set<Integer> getPublicationIds() {
        return publicationIds;
    }

    public void setPublicationIds(Set<Integer> publicationIds) {
        this.publicationIds = publicationIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorDTO authorDTO = (AuthorDTO) o;

        if (publicationCount != authorDTO.publicationCount) return false;
        if (id != null ? !id.equals(authorDTO.id) : authorDTO.id != null) return false;
        if (name != null ? !name.equals(authorDTO.name) : authorDTO.name != null) return false;
        return !(initials != null ? !initials.equals(authorDTO.initials) : authorDTO.initials != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (initials != null ? initials.hashCode() : 0);
        result = 31 * result + publicationCount;
        return result;
    }

    @Override
    public String toString() {
        return "AuthorDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", initials='" + initials + '\'' +
                ", publicationCount=" + publicationCount +
                ", publicationIds=" + publicationIds +
                '}';
    }
}
