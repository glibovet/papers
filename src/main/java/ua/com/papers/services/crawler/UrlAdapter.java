package ua.com.papers.services.crawler;

import lombok.SneakyThrows;
import lombok.experimental.var;
import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.convert.IPartAdapter;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * Transforms element into {@linkplain URL}
 * Created by Максим on 1/8/2017.
 */
public final class UrlAdapter implements IPartAdapter<URL> {

    @Override
    @NotNull
    @SneakyThrows
    public URL convert(@NotNull Element element, @NotNull Page page) {
        var urlSpec = element.absUrl("href");

        if (urlSpec.isEmpty()) {
            // maybe it was a relative link?
            val pageUrl = page.getUrl();

            element.setBaseUri(String.format("%s://%s", pageUrl.getProtocol(), pageUrl.getHost()));
            urlSpec = element.absUrl("href");

            if (urlSpec.isEmpty()) {
                /*was invalid url, see doc*/
                throw new IllegalArgumentException(String.format("Couldn't parse url from element %s", element));
            }
        }
        return new URL(urlSpec);
    }
}
