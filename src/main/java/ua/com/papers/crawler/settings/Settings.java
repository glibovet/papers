package ua.com.papers.crawler.settings;

import com.google.common.base.Preconditions;
import ua.com.papers.crawler.util.IBuilder;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 *     This class represents crawler settings, which
 *     can be used to create a new instance
 * </p>
 * Created by Максим on 11/27/2016.
 */
public final class Settings {

    private final SchedulerSetting schedulerSetting;
    private final Collection<URL> startUrls;
    private final Collection<PageSetting> pageSettings;

    public static final class Builder implements IBuilder<Settings> {

        private final SchedulerSetting schedulerSetting;
        private final Collection<URL> startUrls;
        private final Collection<PageSetting> pageSettings;

        public Builder(@NotNull SchedulerSetting schedulerSetting,
                       @NotNull Collection<URL> startUrls,
                       @NotNull Collection<PageSetting> pageSettings) {
            this.schedulerSetting = Preconditions.checkNotNull(schedulerSetting);
            this.startUrls = new ArrayList<>(Preconditions.checkNotNull(startUrls));
            this.pageSettings = new ArrayList<>(Preconditions.checkNotNull(pageSettings));
        }

        public SchedulerSetting getSchedulerSetting() {
            return schedulerSetting;
        }

        public Collection<URL> getStartUrls() {
            return Collections.unmodifiableCollection(startUrls);
        }

        public Collection<PageSetting> getPageSettings() {
            return Collections.unmodifiableCollection(pageSettings);
        }

        public Builder addStartUrl(@NotNull URL url) {
            startUrls.add(Preconditions.checkNotNull(url));
            return this;
        }

        public Builder addStartUrl(@NotNull Collection<URL> url) {
            startUrls.addAll(Preconditions.checkNotNull(url));
            return this;
        }

        public Builder removeStartUrl(@NotNull URL url) {
            startUrls.remove(Preconditions.checkNotNull(url));
            return this;
        }

        public Builder addPageSetting(@NotNull PageSetting setting) {
            pageSettings.add(Preconditions.checkNotNull(setting));
            return this;
        }

        public Builder addPageSetting(@NotNull Collection<PageSetting> settings) {
            pageSettings.addAll(Preconditions.checkNotNull(settings));
            return this;
        }

        public Builder removeStartUrl(@NotNull PageSetting setting) {
            pageSettings.remove(Preconditions.checkNotNull(setting));
            return this;
        }

        @Override
        public Settings build() {
            return new Settings(this);
        }
    }

    private Settings(Builder builder) {
        this.schedulerSetting = builder.getSchedulerSetting();
        this.startUrls = Collections.unmodifiableCollection(builder.getStartUrls());
        this.pageSettings = Collections.unmodifiableCollection(builder.getPageSettings());
    }

    public SchedulerSetting getSchedulerSetting() {
        return schedulerSetting;
    }

    public Collection<URL> getStartUrls() {
        return startUrls;
    }

    public Collection<PageSetting> getPageSettings() {
        return pageSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Settings settings = (Settings) o;

        if (!schedulerSetting.equals(settings.schedulerSetting)) return false;
        if (!startUrls.equals(settings.startUrls)) return false;
        return pageSettings.equals(settings.pageSettings);

    }

    @Override
    public int hashCode() {
        int result = schedulerSetting.hashCode();
        result = 31 * result + startUrls.hashCode();
        result = 31 * result + pageSettings.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "schedulerSetting=" + schedulerSetting +
                ", startUrls=" + startUrls +
                ", pageSettings=" + pageSettings +
                '}';
    }
}
