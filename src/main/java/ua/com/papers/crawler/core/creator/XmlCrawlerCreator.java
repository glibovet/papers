package ua.com.papers.crawler.core.creator;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import ua.com.papers.crawler.DefaultCrawlerFactory;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;
import ua.com.papers.crawler.util.ICrawlerFactory;

import java.io.File;

/**
 * <p>
 *     Creates crawler from xml settings
 * </p>
 * Created by Максим on 1/9/2017.
 */
@Value
@Getter(value = AccessLevel.NONE)
public class XmlCrawlerCreator implements ICreator {

    File file;
    ICrawlerFactory factory;

    public XmlCrawlerCreator(String location, ICrawlerFactory factory) {
        this(new File(location), factory);
    }

    public XmlCrawlerCreator(File file, ICrawlerFactory factory) {
        Preconditions.checkNotNull(file, "file == null");
        Preconditions.checkArgument(file.exists() && file.isFile(), "File doesn't exist or points to a directory");

        this.file = file;
        this.factory = factory == null ? DefaultCrawlerFactory.getInstance() : factory;
    }

    @Override
    public ICrawlerManager create() {



        return null;
    }
}
