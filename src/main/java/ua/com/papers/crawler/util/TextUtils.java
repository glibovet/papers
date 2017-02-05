package ua.com.papers.crawler.util;

/**
 * Created by Максим on 2/6/2017.
 */
public final class TextUtils {

    private TextUtils() {
        throw new RuntimeException("shouldn't be invoked");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
