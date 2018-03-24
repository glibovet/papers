package ua.com.papers.crawler.util;

import com.google.common.base.Preconditions;
import lombok.val;
import org.joda.time.DateTime;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import ua.com.papers.crawler.core.main.bo.Page;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Максим on 1/6/2017.
 */
public final class PageUtils {

    private static final int PARSE_PAGE_TIMEOUT = 5_000;

    private PageUtils() {
        throw new RuntimeException("shouldn't be invoked");
    }

    /**
     * checks whether content can be transformed into {@linkplain org.jsoup.nodes.Document}
     */
    public static boolean canParse(@NotNull String contentType) {
        Preconditions.checkNotNull(contentType);
        return contentType.startsWith("text/") ||
                contentType.equalsIgnoreCase("application/xml") ||
                contentType.equalsIgnoreCase("application/xhtml+xml");
    }

    public static Page parsePage(@NotNull URL url) throws IOException {
        return parsePage(url, PARSE_PAGE_TIMEOUT);
    }

    public static Page parsePage(@NotNull URL url, int timeout) throws IOException {

        val con = HttpConnection.connect(url)
                .timeout(timeout)
                .ignoreContentType(true)
                .method(Connection.Method.GET);

        val resp = con.execute();

        return Page.builder()
                .charset(resp.charset())
                .content(resp.body())
                .contentType(resp.contentType())
                .url(url)
                .visitTime(DateTime.now())
                .build();
    }

}
