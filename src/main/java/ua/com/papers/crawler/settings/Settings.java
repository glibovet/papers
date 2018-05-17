package ua.com.papers.crawler.settings;

import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.net.URL;
import java.util.Collection;

/**
 * This class represents crawler settings that
 * can be used to configure crawler
 */
@Value
public final class Settings {

    JobId job;
    SchedulerSetting schedulerSetting;
    @Singular
    Collection<? extends URL> startUrls;
    @Singular
    Collection<? extends PageSetting> pageSettings;

    public Settings(@NonNull JobId id,
                    @NonNull SchedulerSetting schedulerSetting,
                    @NonNull Collection<? extends URL> startUrls,
                    @NonNull Collection<? extends PageSetting> pageSettings) {
        this.schedulerSetting = schedulerSetting;
        this.job = id;
        this.startUrls = startUrls;
        this.pageSettings = pageSettings;
    }

}
