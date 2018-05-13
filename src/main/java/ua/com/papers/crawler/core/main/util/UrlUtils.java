package ua.com.papers.crawler.core.main.util;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

public final class UrlUtils {

    private UrlUtils() {
        throw new RuntimeException("Shouldn't be invoked");
    }

    private static Collection<Integer> REDIRECT_RESPONSES = Arrays.asList(
            HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_SEE_OTHER
    );

    @SneakyThrows
    @NonNull
    public static ContentType getContentType(@NonNull URL url) {
        val connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("HEAD");

        if (isRedirect(connection.getResponseCode())) {
            return getContentType(new URL(connection.getHeaderField("Location")));
        }

        return new ContentType(connection.getContentType());
    }

    private static boolean isRedirect(int statusCode) {
        return statusCode != HttpURLConnection.HTTP_OK && REDIRECT_RESPONSES.contains(statusCode);
    }

}
