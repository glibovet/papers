package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.NonNull;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.settings.PageSetting;

/**
 * Subclasses which implement this interface should invoke corresponding underlying handler's
 * method and pass given page as argument
 */
public interface Invoker {
    /**
     * Implement to pass given page to an underlying handler
     *  @param page page to pass
     * @param settings
     */
    void invoke(@NonNull Page page, PageSetting settings);
}
