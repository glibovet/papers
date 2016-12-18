package ua.com.papers.crawler.core.domain.bo;

import lombok.Value;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;

import java.net.URL;

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
    // page content
    Document document;
}
