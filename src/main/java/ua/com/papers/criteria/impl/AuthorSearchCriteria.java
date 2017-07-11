package ua.com.papers.criteria.impl;

import ua.com.papers.criteria.Criteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.pojo.entities.AuthorEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 10.07.2017.
 */
public class AuthorSearchCriteria extends Criteria<AuthorEntity> {
    private String last_name;
    private String first_name;
    private String father_name;

    public AuthorSearchCriteria(String restriction) throws WrongRestrictionException{
        this(0,0,restriction);
    }

    public AuthorSearchCriteria(int offset, int limit, String restriction) throws WrongRestrictionException{
        super(offset,limit, AuthorEntity.class);
        AuthorSearchCriteria parsed = parse(restriction, AuthorSearchCriteria.class);
        if (parsed!=null){
            this.last_name = parsed.last_name;
            this.first_name = parsed.first_name;
            this.father_name = parsed.father_name;
        }
        if (this.last_name ==null||"".equals(this.last_name))
            throw new WrongRestrictionException();
        last_name = last_name.trim();
        if (first_name != null)
            first_name = first_name.trim();
        if (father_name != null)
            father_name = father_name.trim();
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
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
        List<Predicate> conditions = new ArrayList<Predicate>();

        Expression<String> expression;

        if (last_name != null) {
            expression = root.get("lastName");
            conditions.add(cb.equal(expression, last_name));
        }

        if (first_name!=null&&father_name!=null){
            String initials = first_name.charAt(0)+". "+father_name.charAt(0);
            expression = root.get("initials");
            conditions.add(cb.equal(expression, initials));
        }else if (first_name!=null){
            String initials = first_name.charAt(0)+"%";
            expression = root.get("initials");
            conditions.add(cb.like(expression, initials));
        }else if (father_name!=null){
            String initials = "%"+father_name.charAt(0);
            expression = root.get("initials");
            conditions.add(cb.like(expression, initials));
        }

        Predicate[] predicates = conditions.toArray(new Predicate[conditions.size()]);
        query.where(cb.and(predicates));
    }

}
