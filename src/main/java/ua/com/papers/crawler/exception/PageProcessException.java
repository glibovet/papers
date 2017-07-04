package ua.com.papers.crawler.exception;

import java.net.URL;

/**
 * Created by Максим on 4/14/2017.
 */
public class PageProcessException extends RuntimeException {

    private final URL url;

    public PageProcessException(URL url) {
        super();
        this.url = url;
    }

    public PageProcessException(String message, URL url) {
        super(message);
        this.url = url;
    }

    public PageProcessException(String message, Throwable cause, URL url) {
        super(message, cause);
        this.url = url;
    }

    public PageProcessException(Throwable cause, URL url) {
        super(cause);
        this.url = url;
    }

    protected PageProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, URL url) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }
}
