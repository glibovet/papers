package ua.com.papers.services.crawler.unit.repo;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Table(name = "pending_jobs")
@Entity
@Data
@AllArgsConstructor
public class JobEntity {

    @Id
    @Column(name = "url")
    String url;

    @Column(name = "inserted_at")
    Date lastVisit;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    JobStatus status;

    @Column(name = "job")
    String job;

    public JobEntity() {
    }

}
