package ua.com.papers.crawler;

import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.format.convert.IPartAdapter;
import ua.com.papers.crawler.util.Url;

import javax.validation.constraints.NotNull;

/**
 * example
 * Created by Максим on 1/8/2017.
 */
public class ImageUrlAdapter implements IPartAdapter<Url> {

    @Override
    public Url convert(@NotNull Element element) {
        val url = element.absUrl("href");
        System.out.println(url);
        return url.length() == 0 ? null : new Url(url);
    }
}
