package ua.com.papers.criteria.impl;

import ua.com.papers.criteria.Criteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.enums.UploadStatus;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Oleh on 20.12.2016.
 */
public class PublicationCriteria extends Criteria<PublicationEntity> {

    private String query;
    private String link;
    private String title;
    private Collection<Integer> ids;
    private Collection<Integer> authors_id;
    private Integer authorId;
    private Collection<Integer> publishers_id;
    private PublicationStatusEnum status;
    private PublicationTypeEnum type;
    private UploadStatus upload_status;
    private Boolean in_index;

    public PublicationCriteria(){
        super(0, 0, PublicationEntity.class);
    }

    public PublicationCriteria(String restriction) throws WrongRestrictionException {
        this(0, 0, restriction);
    }

    public PublicationCriteria(int offset, int limit, String restriction) throws WrongRestrictionException {
        super(offset, limit, PublicationEntity.class);

        PublicationCriteria parsed = parse(restriction, PublicationCriteria.class);
        if (parsed != null) {
            this.query = parsed.query;
            this.ids = parsed.ids;
            this.authors_id = parsed.authors_id;
            this.publishers_id = parsed.publishers_id;
            this.status = parsed.status;
            this.type = parsed.type;
            this.link = parsed.link;
            this.title = parsed.title;
            this.in_index = parsed.in_index;
            this.upload_status = parsed.upload_status;
        }
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public PublicationCriteria authorId(Integer authorId) {
        this.authorId = authorId;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public PublicationCriteria query(String query) {
        this.query = query;
        return this;
    }

    public Collection<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public PublicationCriteria ids(Collection<Integer> ids) {
        this.ids = ids;
        return this;
    }

    public Collection<Integer> getAuthors_id() {
        return authors_id;
    }

    public void setAuthors_id(List<Integer> authors_id) {
        this.authors_id = authors_id;
    }

    public PublicationCriteria authorsId(List<Integer> authors_id) {
        this.authors_id = authors_id;
        return this;
    }

    public Collection<Integer> getPublishers_id() {
        return publishers_id;
    }

    public void setPublishers_id(List<Integer> publishers_id) {
        this.publishers_id = publishers_id;
    }

    public PublicationCriteria publishersId(List<Integer> publishers_id) {
        this.publishers_id = publishers_id;
        return this;
    }

    public PublicationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PublicationStatusEnum status) {
        this.status = status;
    }

    public PublicationCriteria status(PublicationStatusEnum status) {
        this.status = status;
        return this;
    }

    public PublicationTypeEnum getType() {
        return type;
    }

    public void setType(PublicationTypeEnum type) {
        this.type = type;
    }

    public PublicationCriteria type(PublicationTypeEnum type) {
        this.type = type;
        return this;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public PublicationCriteria link(String link) {
        this.link = link;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PublicationCriteria title(String title) {
        this.title = title;
        return this;
    }

    public Boolean getIn_index() {
        return in_index;
    }

    public void setIn_index(Boolean in_index) {
        this.in_index = in_index;
    }

    public UploadStatus getUpload_status() {
        return upload_status;
    }

    public void setUpload_status(UploadStatus upload_status) {
        this.upload_status = upload_status;
    }

    public PublicationCriteria inIndex(Boolean in_index) {
        this.in_index = in_index;
        return this;
    }

    @Override
    public Query createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PublicationEntity> query = cb.createQuery(PublicationEntity.class);

        Root<PublicationEntity> root = query.from(PublicationEntity.class);
        query.select(root);

        query(query, root, cb);

        return em.createQuery(query);
    }

    @Override
    public Query createCountQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<PublicationEntity> root = query.from(PublicationEntity.class);
        query.select(cb.count(root));

        query(query, root, cb);

        return em.createQuery(query);
    }

    private void query(CriteriaQuery query, Root<PublicationEntity> root, CriteriaBuilder cb){
        List<Predicate> conditions = new ArrayList<Predicate>();
        if (this.ids != null && !this.ids.isEmpty()) {
            Expression<Integer> exception = root.get("id");
            conditions.add(exception.in(this.ids));
            //query.where(exception.in(this.ids));
        }

        if (this.query != null && !this.query.isEmpty()) {
            String likeQuery = '%' + this.query + '%';

            Expression<String> expression = root.get("title");
            Predicate p1 = cb.like(expression, likeQuery);

            expression = root.get("annotation");
            Predicate p2 = cb.like(expression, likeQuery);

            expression = root.get("link");
            Predicate p3 = cb.like(expression, likeQuery);

            expression = root.get("fileNameOriginal");
            Predicate p4 = cb.like(expression, likeQuery);
            conditions.add(cb.or(p1, p2, p3, p4));
            //query.where(cb.or(p1, p2, p3, p4));
        }

        if (this.link != null && !this.link.isEmpty()) {
            Expression<String> expression = root.get("link");
            conditions.add(cb.equal(expression, link));
            //query.where(cb.equal(expression, link));
        }

        if (this.title != null && !this.title.isEmpty()) {
            Expression<String> expression = root.get("title");
            conditions.add(cb.equal(expression, this.title));
            //query.where(cb.equal(expression, this.title));
        }

        if (this.status != null) {
            Expression<PublicationStatusEnum> expression = root.get("status");
            conditions.add(cb.equal(expression, this.status));
            //query.where(cb.equal(expression, this.status));
        }

        if (this.type != null) {
            Expression<PublicationTypeEnum> expression = root.get("type");
            conditions.add(cb.equal(expression, this.type));
            //query.where(cb.equal(expression, this.type));
        }

        if (this.in_index != null) {
            Expression<String> expression = root.get("inIndex");
            conditions.add(cb.equal(expression, this.in_index));
            //query.where(cb.equal(expression, this.in_index));
        }

        if (this.authors_id != null && !this.authors_id.isEmpty()) {
            Join<PublicationEntity, AuthorMasterEntity> authors = root.join("authors");
            conditions.add(authors.get("id").in(this.authors_id));
            //query.where(authors.get("id").in(this.authors_id));
        }

        if (this.publishers_id != null && !this.publishers_id.isEmpty()) {
            Join<PublicationEntity, PublisherEntity> publisher = root.join("publisher");
            conditions.add(publisher.get("id").in(this.publishers_id));
            //query.where(publisher.get("id").in(this.publishers_id));
        }
        if (this.authorId!=null){
            Join<PublicationEntity, AuthorMasterEntity> authors = root.join("authors");
            conditions.add(authors.get("id").in(this.authorId));
        }
        if(this.upload_status!=null){
            Expression<UploadStatus> expression = root.get("uploadStatus");
            conditions.add(cb.equal(expression, this.upload_status));
        }
        Predicate[] predicates = conditions.toArray(new Predicate[conditions.size()]);
        query.where(cb.and(predicates));
    }
}
