package ua.com.papers.criteria;

import com.google.gson.Gson;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by oleh_kurpiak on 14.10.2016.
 */
public abstract class Criteria<T> {

    private int offset;
    private int limit;

    private Class<T> entityClass;

    public Criteria(int offset, int limit, Class<T> entityClass) {
        this.offset = offset;
        this.limit = limit;
        this.entityClass = entityClass;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public abstract Query createQuery(EntityManager em);

    public abstract Query createCountQuery(EntityManager em);

    /**
     * return null if {#restriction} is empty or null
     *
     * @param restriction - json representation of object
     * @param clazz - class of object
     * @param <T> - type that extend Criteria
     * @return
     * @throws WrongRestrictionException - if passed wrong json format
     */
    protected <T extends Criteria> T parse(String restriction, Class<T> clazz) throws WrongRestrictionException {
        if(restriction == null || restriction.isEmpty() || restriction.equals("{}"))
            return null;

        try {
            Gson gson = new Gson();
            T parsed = gson.fromJson(restriction, clazz);

            return parsed;
        } catch (Exception e){
            throw new WrongRestrictionException();
        }
    }
}
