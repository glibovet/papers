package ua.com.papers.crawler.core.processor;

import org.springframework.validation.annotation.Validated;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.processor.convert.IPartAdapter;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.core.processor.exception.ProcessException;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by Максим on 12/19/2016.
 */
@Validated
public interface IFormatManager {

    void registerAdapter(@NotNull IPartAdapter<?> adapter);

    void unregisterAdapter(@NotNull Class<? extends IPartAdapter<?>> cl);

    @NotNull
    Set<? extends IPartAdapter<?>> getRegisteredAdapters();

    void processPage(@NotNull PageID pageID, @NotNull Page page) throws ProcessException;

}
