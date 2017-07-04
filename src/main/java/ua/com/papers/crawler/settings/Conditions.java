package ua.com.papers.crawler.settings;

/**
 * Created by Максим on 4/11/2017.
 */
public final class Conditions {

    private Conditions() {
        throw new IllegalStateException("shouldn't be called");
    }

    public static void checkArgument(boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static void checkArgument(boolean condition) {
        if (!condition)
            throw new IllegalArgumentException();
    }

    public static <T> void isNull(T t, String message) {
        if (t != null)
            throw new IllegalArgumentException(message);
    }

    public static <T> void isNull(T t) {
        if (t != null)
            throw new NullPointerException();
    }

    public static <T> T isNotNull(T t, String message) {
        if (t == null)
            throw new NullPointerException(message);

        return t;
    }

    public static <T> T isNotNull(T t) {
        if (t == null)
            throw new NullPointerException();

        return t;
    }

}
