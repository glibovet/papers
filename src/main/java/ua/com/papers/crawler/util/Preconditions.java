package ua.com.papers.crawler.util;

/**
 * Created by Максим on 4/18/2017.
 */
public final class Preconditions {

    private Preconditions() {
        throw new IllegalStateException("shouldn't be invoked");
    }

    public static <T> T checkNotNull(T t) {

        if (t == null)
            throw new NullPointerException();

        return t;
    }

    public static <T> T checkNotNull(T t, String message) {

        if (t == null)
            throw new NullPointerException(message);

        return t;
    }

    public static void checkArgument(boolean condition, String message) {

        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static void checkArgument(boolean condition) {

        if (!condition)
            throw new IllegalArgumentException();
    }
}
