package ua.com.papers.criteria.impl;

import ua.com.papers.criteria.Criteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.entities.PublicationOrderEntity;
import ua.com.papers.pojo.enums.PublicationOrderStatusEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by Andrii on 20.05.2017.
 */
public class PublicationOrderCriteria extends Criteria<PublicationOrderEntity> {

    private String query;
    private List<Integer> ids;
    private List<Integer> publication_ids;
    private PublicationOrderStatusEnum status;
    private String email;

    public PublicationOrderCriteria(String restriction) throws WrongRestrictionException {
        this(0,0,restriction);
    }

    public PublicationOrderCriteria(int offset, int limit, String restriction) throws WrongRestrictionException {
        super(offset, limit, PublicationOrderEntity.class);

        PublicationOrderCriteria parsed = parse(restriction, PublicationOrderCriteria.class);
        if (parsed != null) {
            this.query = parsed.query;
            this.ids = parsed.ids;
            this.publication_ids = parsed.publication_ids;
            this.status = parsed.status;
            this.email = parsed.email;
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Integer> getPublication_ids() {
        return publication_ids;
    }

    public void setPublication_ids(List<Integer> publication_ids) {
        this.publication_ids = publication_ids;
    }

    public PublicationOrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PublicationOrderStatusEnum status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Query createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PublicationOrderEntity> query = cb.createQuery(PublicationOrderEntity.class);

        Root<PublicationOrderEntity> root = query.from(PublicationOrderEntity.class);
        query.select(root);

        query(query, root, cb);

        return em.createQuery(query);
    }

    @Override
    public Query createCountQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<PublicationOrderEntity> root = query.from(PublicationOrderEntity.class);
        query.select(cb.count(root));

        query(query, root, cb);

        return em.createQuery(query);
    }

    private void query(CriteriaQuery query, Root<PublicationOrderEntity> root, CriteriaBuilder cb){
        if (this.ids != null && !this.ids.isEmpty()) {
            Expression<Integer> exception = root.get("id");
            query.where(exception.in(this.ids));
        }

        if (this.query != null && !this.query.isEmpty()) {
            String likeQuery = '%' + this.query + '%';

            Expression<String> expression = root.get("email");
            Predicate p1 = cb.like(expression, likeQuery);

            expression = root.get("reason");
            Predicate p2 = cb.like(expression, likeQuery);

            expression = root.get("answer");
            Predicate p3 = cb.like(expression, likeQuery);

            query.where(cb.or(p1, p2, p3));
        }

        if (this.email != null && !this.email.isEmpty()) {
            Expression<String> expression = root.get("email");
            query.where(cb.equal(expression, email));
        }

        if (this.status != null) {
            Expression<String> expression = root.get("status");
            query.where(cb.equal(expression, this.status));
        }


        if (this.publication_ids != null && !this.publication_ids.isEmpty()) {
            Join<PublicationOrderEntity, PublicationEntity> publication = root.join("publication");
            query.where(publication.get("id").in(this.publication_ids));
        }
    }

}
