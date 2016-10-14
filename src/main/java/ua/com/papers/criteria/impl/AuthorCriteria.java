package ua.com.papers.criteria.impl;

import ua.com.papers.criteria.Criteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by oleh_kurpiak on 14.10.2016.
 */
public class AuthorCriteria extends Criteria<AuthorEntity> {

    private String query;

    private List<Integer> ids;

    private List<Integer> master_ids;

    public AuthorCriteria(String restriction) throws WrongRestrictionException {
        this(0, 0, restriction);
    }

    public AuthorCriteria(int offset, int limit, String restriction) throws WrongRestrictionException {
        super(offset, limit, AuthorEntity.class);

        AuthorCriteria parsed = parse(restriction, AuthorCriteria.class);
        if (parsed != null) {
            this.query = parsed.query;
            this.ids = parsed.ids;
            this.master_ids = parsed.master_ids;
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

    public List<Integer> getMaster_ids() {
        return master_ids;
    }

    public void setMaster_ids(List<Integer> master_ids) {
        this.master_ids = master_ids;
    }

    @Override
    public Query createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AuthorEntity> query = cb.createQuery(AuthorEntity.class);

        Root<AuthorEntity> root = query.from(AuthorEntity.class);
        query.select(root);

        query(query, root, cb);

        return em.createQuery(query);
    }

    @Override
    public Query createCountQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<AuthorEntity> root = query.from(AuthorEntity.class);
        query.select(cb.count(root));

        query(query, root, cb);

        return em.createQuery(query);
    }

    private void query(CriteriaQuery query, Root<AuthorEntity> root, CriteriaBuilder cb){
        if (this.ids != null && !this.ids.isEmpty()) {
            Expression<Integer> expression = root.get("id");
            query.where(expression.in(this.ids));
        }

        if (this.query != null && !this.query.isEmpty()) {
            String likeQuery = '%'+this.query+'%';

            Expression<String> expression = root.get("lastName");
            Predicate lastName = cb.like(expression, likeQuery);

            expression = root.get("initials");
            Predicate initials = cb.like(expression, likeQuery);

            expression = root.get("original");
            Predicate original = cb.like(expression, likeQuery);

            query.where(cb.or(lastName, initials, original));
        }

        if (this.master_ids != null && !this.master_ids.isEmpty()) {
            Join<AuthorEntity, AuthorMasterEntity> master = root.join("master");
            query.where(master.get("id").in(this.master_ids));
        }
    }
}
