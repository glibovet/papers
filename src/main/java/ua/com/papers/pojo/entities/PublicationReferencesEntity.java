package ua.com.papers.pojo.entities;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oleh_kurpiak on 15.09.2016.
 */
@NodeEntity
public class PublicationReferencesEntity {

    @GraphId
    private Integer id;

    private String test;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicationReferencesEntity that = (PublicationReferencesEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return test != null ? test.equals(that.test) : that.test == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (test != null ? test.hashCode() : 0);
        return result;
    }
}
