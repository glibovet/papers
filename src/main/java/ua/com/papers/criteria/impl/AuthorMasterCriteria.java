package ua.com.papers.criteria.impl;

import ua.com.papers.criteria.Criteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleh_kurpiak on 14.10.2016.
 */
public class AuthorMasterCriteria extends Criteria<AuthorMasterEntity> {

    private String query;
    private String lastName;
    private String initials;

    private List<Integer> ids;

    private List<Integer> sub_ids;

    private Boolean has_sub;

    public AuthorMasterCriteria(String restriction) throws WrongRestrictionException {
        this(0, 0, restriction);
    }

    public AuthorMasterCriteria(int offset, int limit){
        super(offset, limit, AuthorMasterEntity.class);
    }

    public AuthorMasterCriteria(int offset, int limit, String restriction) throws WrongRestrictionException {
        super(offset, limit, AuthorMasterEntity.class);

        AuthorMasterCriteria parsed = parse(restriction, AuthorMasterCriteria.class);
        if(parsed != null) {
            this.query = parsed.query;
            this.ids = parsed.ids;
            this.sub_ids = parsed.sub_ids;
            this.has_sub = parsed.has_sub;
        }
    }

    @Override
    public Query createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AuthorMasterEntity> query = cb.createQuery(AuthorMasterEntity.class);

        Root<AuthorMasterEntity> root = query.from(AuthorMasterEntity.class);
        query.select(root);

        query(query, root, cb);

        return em.createQuery(query);
    }

    @Override
    public Query createCountQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<AuthorMasterEntity> root = query.from(AuthorMasterEntity.class);
        query.select(cb.count(root));

        query(query, root, cb);

        return em.createQuery(query);
    }

    private void query(CriteriaQuery query, Root<AuthorMasterEntity> root, CriteriaBuilder cb){
        List<Predicate> conditions = new ArrayList<Predicate>();
        if (this.ids != null && !this.ids.isEmpty()) {
            Expression<Integer> expression = root.get("id");
            conditions.add(expression.in(this.ids));
            //query.where(expression.in(this.ids));
        }

        if (this.query != null && !this.query.isEmpty()) {
            String likeQuery = '%'+this.query+'%';
            Expression<String> expression = root.get("lastName");
            Predicate lastName = cb.like(expression, likeQuery);

            expression = root.get("initials");
            Predicate initials = cb.like(expression, likeQuery);
            conditions.add(cb.or(lastName, initials));
            //query.where(cb.or(lastName, initials));
        }

        if (this.lastName !=null){
            Expression<String> expression = root.get("lastName");
            Predicate predicate = cb.equal(expression, this.lastName);
            conditions.add(predicate);
            //query.where(predicate);
        }
        if (this.initials !=null){
            Expression<String> expression = root.get("initials");
            Predicate predicate = cb.equal(expression, this.initials);
            conditions.add(predicate);
            //query.where(predicate);
        }
        if (this.sub_ids != null && !this.sub_ids.isEmpty()) {
            Join<AuthorMasterEntity, AuthorEntity> subAuthors = root.join("authors");
            conditions.add(subAuthors.get("id").in(this.sub_ids));
            //query.where(subAuthors.get("id").in(this.sub_ids));
        }

        if (this.has_sub != null) {
            Expression<List<AuthorEntity>> expression = root.get("authors");
            Predicate predicate;
            if (this.has_sub) {
                predicate = cb.ge(cb.size(expression), 1);
            } else {
                predicate = cb.equal(cb.size(expression), 0);
            }
            conditions.add(predicate);
            //query.where(predicate);
        }
        Predicate[] predicates = conditions.toArray(new Predicate[conditions.size()]);
        query.where(cb.and(predicates));
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
}
