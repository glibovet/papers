package ua.com.papers.persistence.criteria;

import ua.com.papers.criteria.Criteria;

import java.util.List;

/**
 * Created by oleh_kurpiak on 14.10.2016.
 */
public interface ICriteriaRepository{

    <T> List<T> find(Criteria<T> criteria);

    <T> int count(Criteria<T> criteria);
}
