package ua.com.papers.criteria.impl;

import ua.com.papers.criteria.Criteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.pojo.entities.AddressEntity;
import ua.com.papers.pojo.entities.PublisherEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by oleh_kurpiak on 15.10.2016.
 */
public class PublisherCriteria extends Criteria<PublisherEntity> {

    private String query;
    private List<Integer> ids;
    private List<Integer> address_ids;

    public PublisherCriteria(String restriction) throws WrongRestrictionException {
        this(0, 0, restriction);
    }

    public PublisherCriteria(int offset, int limit, String restriction) throws WrongRestrictionException {
        super(offset, limit, PublisherEntity.class);

        PublisherCriteria parsed = parse(restriction, PublisherCriteria.class);
        if (parsed != null) {
            this.query = parsed.query;
            this.ids = parsed.ids;
            this.address_ids = parsed.address_ids;
        }
    }

    @Override
    public Query createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PublisherEntity> query = cb.createQuery(PublisherEntity.class);

        Root<PublisherEntity> root = query.from(PublisherEntity.class);
        query.select(root);

        query(query, root, cb);

        return em.createQuery(query);
    }

    @Override
    public Query createCountQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<PublisherEntity> root = query.from(PublisherEntity.class);
        query.select(cb.count(root));

        query(query, root, cb);

        return em.createQuery(query);
    }

    private void query(CriteriaQuery query, Root<PublisherEntity> root, CriteriaBuilder cb){
        if (this.ids != null && !this.ids.isEmpty()) {
            Expression<Integer> exception = root.get("id");
            query.where(exception.in(this.ids));
        }

        if (this.query != null && !this.query.isEmpty()) {
            String likeQuery = '%' + this.query + '%';

            Expression<String> expression = root.get("title");
            Predicate p1 = cb.like(expression, likeQuery);

            expression = root.get("description");
            Predicate p2 = cb.like(expression, likeQuery);

            expression = root.get("url");
            Predicate p3 = cb.like(expression, likeQuery);

            expression = root.get("contacts");
            Predicate p4 = cb.like(expression, likeQuery);

            query.where(cb.or(p1, p2, p3, p4));
        }

        if (this.address_ids != null && !this.address_ids.isEmpty()) {
            Join<PublisherEntity, AddressEntity> address = root.join("address");
            query.where(address.get("id").in(this.address_ids));
        }
    }
}
