package ua.com.papers.crawler.core.domain;

import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by Максим on 12/29/2016.
 */
public interface IPageIndexer {

    interface ICallback {

        default void onStart() {}

        void onIndexed(@NotNull Page page);

        void onUpdated(@NotNull Page page);

        void onLost(@NotNull Page page);

        default void onStop() {}

    }

    void addToIndex(@NotNull Page page);

    void index(@NotNull ICallback callback, @NotNull Collection<Object> handlers);

}
