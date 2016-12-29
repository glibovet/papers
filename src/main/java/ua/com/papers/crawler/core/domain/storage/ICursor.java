package ua.com.papers.crawler.core.domain.storage;

/**
 * Created by Максим on 12/29/2016.
 */
public interface ICursor<T> {

    boolean hasNext();

    boolean first();

    T next();

}
