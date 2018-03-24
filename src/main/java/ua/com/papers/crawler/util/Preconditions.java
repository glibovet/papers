package ua.com.papers.crawler.util;

import java.util.Locale;

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

    public static void checkNotNullAll(Object... o) {
        for (int i = 0; i < o.length; ++i) {
            if (o[i] == null) {
                throw new NullPointerException(String.format(Locale.ENGLISH, "%d-th argument was null", i + 1));
            }
        }
    }

    public static <T> T checkNotNull(T t, String message) {

        if (t == null)
            throw new NullPointerException(message);

        return t;
    }

    public static <T> T checkNotNull(T t, String message, Object...args) {

        if (t == null)
            throw new NullPointerException(String.format(message, args));

        return t;
    }

    public static void checkArgument(boolean condition, String message) {

        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static void checkArgument(boolean condition, String message, Object...args) {

        if (!condition)
            throw new IllegalArgumentException(String.format(message, args));
    }

    public static void checkArgument(boolean condition) {

        if (!condition)
            throw new IllegalArgumentException();
    }
}
