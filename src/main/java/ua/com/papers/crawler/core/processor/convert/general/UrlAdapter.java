package ua.com.papers.crawler.core.processor.convert.general;

import lombok.*;
import lombok.experimental.var;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.convert.ElementConverter;
import ua.com.papers.crawler.settings.PageSetting;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * Transforms element into {@linkplain URL}
 */
public final class UrlAdapter implements ElementConverter<URL> {

    private static final class Holder {
        private static final UrlAdapter INSTANCE = new UrlAdapter();
    }

    public static UrlAdapter getInstance() {
        return Holder.INSTANCE;
    }

    @Getter
    @Setter
    private boolean dynamicResolveUrl = true;

    private UrlAdapter() {
    }

    @Override
    public Class<? extends URL> converts() {
        return URL.class;
    }

    @Override
    @NotNull
    @SneakyThrows
    public URL convert(@NotNull Element element, @NotNull Page page, @NotNull PageSetting settings) {
        var urlSpec = element.absUrl("href");

        if (urlSpec.isEmpty()) {
            // maybe it was a relative link?
            val pageUrl = extractUrlWithBase(page, settings);

            element.setBaseUri(String.format("%s://%s", pageUrl.getProtocol(), pageUrl.getHost()));
            urlSpec = element.absUrl("href");

            if (urlSpec.isEmpty()) {
                /*was invalid url, see doc*/
                throw new IllegalArgumentException(String.format("Couldn't parse url from element %s", element));
            }
        }
        return new URL(urlSpec);
    }

    @NonNull
    private URL extractUrlWithBase(Page page, PageSetting setting) {
        if (dynamicResolveUrl) {
            return page.getUrl();
        }

        return setting.getBaseUrl().orElseThrow(() -> new IllegalStateException(String.format("No base url was specified for page=%s, " +
                "page id=%s", page.getUrl(), setting.getId())));
    }

}
