package ua.com.papers.crawler.util;

/**
 * Created by Максим on 1/6/2017.
 */
public enum MimeType implements ContentType {

    PLAIN("text/plain");

    private final String type;

    MimeType(String type) {
        this.type = type;
    }

    @Override
    public String asStr() {
        return type;
    }

    public static MimeType forId(String id) {



        return null;
    }

}
