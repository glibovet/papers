package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.NonNull;
import ua.com.papers.crawler.settings.v2.process.Handles;

/**
 * Subclasses which implement this interface should invoke corresponding underlying handler's
 * method and pass given page as argument
 */
public interface GroupedInvoker extends Invoker {

    int group();

    @NonNull
    Handles.CallPolicy executionPolicy();

}
