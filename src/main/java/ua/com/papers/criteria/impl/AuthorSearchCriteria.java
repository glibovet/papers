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
    private String lastname;
    private String firstname;
    private String fathername;

    public AuthorSearchCriteria(String restriction) throws WrongRestrictionException{
        this(0,0,restriction);
    }

    public AuthorSearchCriteria(int offset, int limit, String restriction) throws WrongRestrictionException{
        super(offset,limit, AuthorEntity.class);
        AuthorSearchCriteria parsed = parse(restriction, AuthorSearchCriteria.class);
        if (parsed!=null){
            this.lastname = parsed.lastname;
            this.firstname = parsed.firstname;
            this.fathername = parsed.fathername;
        }
        if (this.lastname ==null||"".equals(this.lastname))
            throw new WrongRestrictionException();
        lastname = lastname.trim();
        firstname = firstname.trim();
        fathername = fathername.trim();
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
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
        Expression<String> expression = root.get("lastName");
        conditions.add(cb.equal(expression, lastname));
        if (firstname!=null&&fathername!=null){
            String initials = firstname.charAt(0)+". "+lastname.charAt(0);
            expression = root.get("initials");
            conditions.add(cb.equal(expression, initials));
        }else if (firstname!=null){
            String initials = firstname.charAt(0)+"%";
            expression = root.get("initials");
            conditions.add(cb.like(expression, initials));
        }else if (fathername!=null){
            String initials = "%"+fathername.charAt(0);
            expression = root.get("initials");
            conditions.add(cb.like(expression, initials));
        }
        Predicate[] predicates = conditions.toArray(new Predicate[conditions.size()]);
        query.where(cb.and(predicates));
    }

}
