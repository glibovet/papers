package ua.com.papers.crawler.core.main.bo;

import lombok.*;
import lombok.experimental.NonFinal;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ua.com.papers.crawler.util.PageUtils;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * <p>
 * Represents single web page with its content
 * </p>
 * Created by Максим on 12/1/2016.
 */
@Value
public class Page {
    // page url
    URL url;
    // visit timestamp
    DateTime visitTime;
    Charset charset;
    // raw page content
    String content;
    // raw content type such
    // as text/plain and so on
    String contentType;

    @NonFinal
    @Getter(AccessLevel.NONE)
    private volatile Document document;
    // sync lock
    private final Object lock = new Object();

    @lombok.Builder(builderClassName = "Builder")
    private Page(@NotNull URL url, @NotNull DateTime visitTime, @NotNull String charset, @NotNull String content,
                 @NotNull String contentType) {
        this.url = url;
        this.visitTime = visitTime;
        this.contentType = contentType;

        val canParse = PageUtils.canParse(contentType);

        this.charset = canParse ? Charset.forName(charset) : null;
        this.content = canParse ? content : null;
    }

    public Document toDocument() {

        if (!PageUtils.canParse(contentType))
            throw new IllegalStateException(
                    String.format("Illegal content type, must be text/*, application/xml, or " +
                            "application/xhtml+xml, was %s", contentType));

        Document localVar = document;

        if (localVar == null) {
            synchronized (lock) {
                document = localVar = Jsoup.parse(content);
            }
        }
        return localVar;
    }
}
