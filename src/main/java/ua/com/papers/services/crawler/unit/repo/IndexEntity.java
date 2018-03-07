package ua.com.papers.services.crawler.unit.repo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

/**
 * Created by Максим on 2/12/2017.
 */
@Entity
@Table(name = "indexes")
@Data
public class IndexEntity {

    @Id
    @Column(name = "url")
    String url;

    @Column(name = "last_visit")
    Date lastVisit;

    @Column(name = "content_hash")
    String contentHash;

    public IndexEntity() {
    }

    public IndexEntity(String url, Date lastVisit, String contentHash) {
        this.url = url;
        this.lastVisit = lastVisit;
        this.contentHash = contentHash;
    }

}
