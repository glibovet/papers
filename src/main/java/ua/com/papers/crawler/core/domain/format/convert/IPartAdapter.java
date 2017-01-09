package ua.com.papers.crawler.core.domain.format.convert;

import org.jsoup.nodes.Element;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 1/8/2017.
 */
public interface IPartAdapter<T> {

    T convert(@NotNull Element element);

}
