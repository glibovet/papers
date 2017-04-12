package ua.com.papers.crawler.core.domain;

import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;

/**
 * Created by Максим on 12/29/2016.
 */
public interface IPageIndexer {

    interface Callback {

        default void onStart() {}

        void onIndexed(@NotNull Page page);

        void onUpdated(@NotNull Page page);

        void onLost(@NotNull Page page);

        void onException(@NotNull URL url, @NotNull Throwable th);

        default void onStop() {}

    }

    void addToIndex(@NotNull Page page);

    void index(@NotNull Callback callback, @NotNull Collection<Object> handlers);

    void stop();

}
