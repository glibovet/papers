package ua.com.papers.services.crawler;

import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.format.convert.IPartAdapter;
import ua.com.papers.crawler.util.Url;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * Transforms element into {@linkplain URL}
 * Created by Максим on 1/8/2017.
 */
public class UrlAdapter implements IPartAdapter<URL> {

    @Override
    public URL convert(@NotNull Element element) {
        val url = element.absUrl("href");
        return url.length() == 0 ? /*was invalid url, see doc*/ null : new Url(url).getUrl();
    }
}
