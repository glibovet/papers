package ua.com.papers.crawler.core.domain;

import com.google.common.base.Preconditions;
import lombok.Cleanup;
import lombok.extern.java.Log;
import org.joda.time.DateTime;
import ua.com.papers.crawler.core.domain.analyze.IAnalyzeManager;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.mPage;
import ua.com.papers.crawler.util.PageHandler;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Максим on 12/1/2016.
 */
@Log
public final class Crawler implements ICrawler {

    private static final Pattern URL_PATTERN = Pattern
            .compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov"
                    + "|mil|biz|info|mobi|name|aero|jobs|museum" + "|travel|[a-z]{2}))(:[\\d]{1,5})?"
                    + "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
                    + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
                    + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

    private final Queue<URL> urls;
    private final Map<URL, Collection<mPage>> crawledPages;
    private final IAnalyzeManager analyzeManager;

    public Crawler(@NotNull Collection<URL> startUrls, @NotNull IAnalyzeManager analyzeManager) {
        Preconditions.checkNotNull(startUrls);
        this.analyzeManager = Preconditions.checkNotNull(analyzeManager);
        this.urls = new LinkedList<>(startUrls);
        this.crawledPages = new HashMap<>(30);
    }

    @Override
    public void start(@Nullable ICallback callback, @NotNull Collection<Object> handlers) {
        checkHandlers(handlers);

        if (callback != null) {
            callback.onStart();
        }

        final int MAX_CONTAINER_SIZE = 100;
        // todo redo
        final Map<URL, Collection<Page>> crawledPages = new HashMap<>(MAX_CONTAINER_SIZE);

        while (!urls.isEmpty()
                /*replace with spec condition*/ && urls.size() <= MAX_CONTAINER_SIZE) {
            final URL url = urls.poll();
            Collection<Page> crawledPagesColl = crawledPages.get(url);

            if (callback != null) {
                callback.onUrlEntered(url);
            }

            try {

                final Page page = new Page(url, Crawler.extractPageContent(url), DateTime.now());

                if (crawledPagesColl == null) {
                    crawledPagesColl = new ArrayList<>(1);
                }

                crawledPagesColl.add(page);
                // overrides map val
                crawledPages.put(url, crawledPagesColl);

                if (analyzeManager.matches(page)) {

                    log.log(Level.INFO, String.format("Accepted page: url %s", url));
                    //todo format page and so on
                    Crawler.extractUrls(page)
                            .stream()
                            .filter(u -> !urls.contains(u) && !crawledPages.containsKey(u))
                            .forEach(urls::add);

                } else {
                    log.log(Level.INFO, String.format("Rejected page: url %s", url));

                    if (callback != null) {
                        callback.onPageRejected(page);
                    }
                }
            } catch (final IOException e) {
                log.log(Level.WARNING, String.format("Failed to extract page content for url %s", url), e);

                if (callback != null) {
                    callback.onException(e);
                }
            }
        }

        if (callback != null) {
            callback.onStop();
        }
    }

    // todo refactor
    private static Set<URL> extractUrls(Page page) {

        final Set<URL> urls = new HashSet<>();
        final Matcher matcher = URL_PATTERN.matcher(page.getContent());

        while (matcher.find()) {

            String strUrl = matcher.group();

            if(strUrl == null || strUrl.length() == 0) continue;

            if(!strUrl.startsWith("http") && !strUrl.startsWith("https")) {
                strUrl = "http://" + strUrl;
            }

            try {
                urls.add(new URL(strUrl));
            } catch (final MalformedURLException e) {
                log.log(Level.WARNING, String.format("Malformed url: %s", strUrl), e);
            }
        }
        return urls;
    }

    @Override
    public void start(@NotNull Collection<Object> handlers) {
        start(null, handlers);
    }

    @Override
    public void stop() {

    }

    @Override
    public void stop(long wait) {

    }

    private static String extractPageContent(URL url) throws IOException {

        @Cleanup final InputStream is = url.openStream();
        @Cleanup final BufferedReader in = new BufferedReader(new InputStreamReader(is));

        final StringBuilder content = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }

    private static void checkHandlers(Collection<Object> handlers) {
        //todo empty check
        for (final Object handler : handlers)
            if (!handler.getClass().isAnnotationPresent(PageHandler.class))
                throw new IllegalArgumentException(
                        String.format("%s class must be annotated with %s", handler.getClass(), PageHandler.class));
    }

}
