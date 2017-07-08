package ua.com.papers.criteria.impl;

import ua.com.papers.criteria.Criteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.pojo.entities.RoleEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.RolesEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by Oleh on 08.07.2017.
 */
public class UserCriteria extends Criteria<UserEntity> {

    private String query;
    private List<Integer> ids;
    private List<RolesEnum> roles;
    private Boolean active;

    public UserCriteria(String restriction) throws WrongRestrictionException {
        this(0, 0, restriction);
    }

    public UserCriteria(int offset, int limit, String restriction) throws WrongRestrictionException {
        super(offset, limit, UserEntity.class);

        UserCriteria parsed = parse(restriction, UserCriteria.class);
        if (parsed != null) {
            this.query = parsed.query;
            this.ids = parsed.ids;
            this.roles = parsed.roles;
        }
    }

    @Override
    public Query createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);

        Root<UserEntity> root = query.from(UserEntity.class);
        query.select(root);

        query(query, root, cb);

        return em.createQuery(query);
    }

    @Override
    public Query createCountQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<UserEntity> root = query.from(UserEntity.class);
        query.select(cb.count(root));

        query(query, root, cb);

        return em.createQuery(query);
    }

    private void query(CriteriaQuery query, Root<UserEntity> root, CriteriaBuilder cb){
        if (this.ids != null && !this.ids.isEmpty()) {
            Expression<Integer> exception = root.get("id");
            query.where(exception.in(this.ids));
        }

        if (this.query != null && !this.query.isEmpty()) {
            String likeQuery = '%' + this.query + '%';

            Expression<String> expression = root.get("email");
            Predicate p1 = cb.like(expression, likeQuery);

            expression = root.get("name");
            Predicate p2 = cb.like(expression, likeQuery);

            query.where(cb.or(p1, p2));
        }

        if (this.roles != null && !this.roles.isEmpty()) {
            Join<UserEntity, RoleEntity> role = root.join("roleEntity");
            query.where(role.get("name").in(this.roles));
        }

        if (this.active != null) {
            Expression<String> expression = root.get("active");
            query.where(cb.equal(expression, this.active));
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

    public List<RolesEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<RolesEnum> roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
