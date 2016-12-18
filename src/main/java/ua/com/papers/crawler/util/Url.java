package ua.com.papers.crawler.util;

import lombok.Value;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * Transforms checked {@linkplain MalformedURLException} into runtime exception
 * </p>
 * Created by Максим on 12/18/2016.
 */
@Value
public class Url {

    URL url;

    public Url(String spec) {
        try {
            this.url = new URL(spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
