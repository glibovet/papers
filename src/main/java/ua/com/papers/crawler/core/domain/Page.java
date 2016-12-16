package ua.com.papers.crawler.core.domain;

import lombok.Value;
import org.joda.time.DateTime;

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
    // raw page content (including html tags)
    String content;
    // visit timestamp
    DateTime visitTime;

}
