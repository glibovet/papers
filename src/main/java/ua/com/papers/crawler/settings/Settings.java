package ua.com.papers.crawler.settings;

import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.net.URL;
import java.util.Collection;

/**
 * <p>
 * This class represents crawler settings, which
 * can be used to create a new instance
 * </p>
 * Created by Максим on 11/27/2016.
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
