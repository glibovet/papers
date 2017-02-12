package ua.com.papers.crawler.test;

import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by Максим on 2/12/2017.
 */
@Component
@Value
public class AppContextProvider {

    ApplicationContext context;

    @Autowired
    public AppContextProvider(ApplicationContext context) {
        this.context = context;
    }

}
