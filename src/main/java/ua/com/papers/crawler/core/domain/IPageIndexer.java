package ua.com.papers.crawler.core.domain;

import ua.com.papers.crawler.core.domain.bo.Page;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by Максим on 12/29/2016.
 */
public interface IPageIndexer {

    interface ICallback {

        default void onStart() {}

        void onPageUpdated(@NotNull Page page);

        void onPageLost(@NotNull Page page);

        default void onStop() {}

    }

    void addToIndex(@NotNull Page page);

    void index(@Nullable ICallback callback, @NotNull Collection<Object> handlers);

}
