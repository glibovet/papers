package ua.com.papers.crawler.core.domain;

import java.net.URL;

/**
 * Created by Максим on 12/1/2016.
 */
class Page {

    private final URL url;
    private final String content;

    public Page(URL url, String content) {
        this.url = url;
        this.content = content;
    }

    public URL getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
