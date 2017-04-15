package ua.com.papers.crawler.settings;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * This class represents crawler settings, which
 * can be used to create a new instance
 * </p>
 * Created by Максим on 11/27/2016.
 */
@Value
@Builder(builderClassName = "Builder")
public final class Settings {

    SchedulerSetting schedulerSetting;
    @Singular
    List<URL> startUrls;
    @Singular
    Collection<PageSetting> pageSettings;

    private Settings(SchedulerSetting schedulerSetting,
                     List<URL> startUrls,
                     Collection<PageSetting> pageSettings) {
        this.schedulerSetting = schedulerSetting;
        this.startUrls = startUrls;
        this.pageSettings = pageSettings;
    }

}
