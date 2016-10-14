package ua.com.papers.criteria.impl;

import com.google.gson.Gson;
import org.springframework.security.core.Authentication;
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
public class AuthorMasterCriteria extends Criteria<AuthorMasterEntity> {

    private String query;

    private List<Integer> ids;

    private List<Integer> sub_ids;

    public AuthorMasterCriteria(String restriction) throws WrongRestrictionException {
        this(0, 0, restriction);
    }

    public AuthorMasterCriteria(int offset, int limit, String restriction) throws WrongRestrictionException {
        super(offset, limit, AuthorMasterEntity.class);

        AuthorMasterCriteria parsed = parse(restriction, AuthorMasterCriteria.class);
        if(parsed != null) {
            this.query = parsed.query;
            this.ids = parsed.ids;
            this.sub_ids = parsed.sub_ids;
        }
    }

    @Override
    public Query createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AuthorMasterEntity> query = cb.createQuery(AuthorMasterEntity.class);

        Root<AuthorMasterEntity> root = query.from(AuthorMasterEntity.class);
        query.select(root);

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
            query.where(cb.or(lastName, initials));
        }

        if (this.sub_ids != null && !this.sub_ids.isEmpty()) {
            Join<AuthorMasterEntity, AuthorEntity> subAuthors = root.join("authors");
            query.where(subAuthors.get("id").in(this.sub_ids));
        }
    }
}
