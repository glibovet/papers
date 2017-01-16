package ua.com.papers.crawler.core.creator.xml;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ua.com.papers.crawler.DefaultCrawlerFactory;
import ua.com.papers.crawler.core.creator.ICreator;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.settings.*;
import ua.com.papers.crawler.util.ICrawlerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * <p>
 * {@linkplain ICreator} implementation that creates crawler from xml file
 * </p>
 * Created by Максим on 1/9/2017.
 */
@Getter(value = AccessLevel.NONE)
public final class XmlCreator extends AbstractClasspathXmlCreator {

    private static final File XSD_LOCATION;

    static {
        XSD_LOCATION = new File("src/main/resources/crawler/xsd/crawler.xsd");
    }

    public XmlCreator(@NotNull String location) {
        this(new File(location), DefaultCrawlerFactory.getInstance());
    }

    public XmlCreator(@NotNull File file, @NotNull ICrawlerFactory factory) {
        super(file, XmlCreator.XSD_LOCATION, Preconditions.checkNotNull(factory));
    }

    @Override
    protected Settings parseFile(@NotNull Document document) {
        val rootElem = document.getDocumentElement();

        return Settings.builder()
                .schedulerSetting(parseSchedulerSettings(rootElem))
                .pageSettings(parsePageSettings(rootElem))
                .startUrls(parseStartUrls(rootElem))
                .build();
    }

    /**
     * @param root xml element to parse urls from
     */
    private List<URL> parseStartUrls(Element root) {
        val nodes = root.getElementsByTagName("url");
        val result = new ArrayList<URL>(nodes.getLength());

        try {
            for (int i = 0; i < nodes.getLength(); ++i) {
                val entry = (Element) nodes.item(i);
                result.add(new URL(entry.getTextContent()));
            }
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * @param root xml element to parse scheduler settings from
     */
    private SchedulerSetting parseSchedulerSettings(Element root) {

        val threadsEl = (Element) root.getElementsByTagName("threads").item(0);
        val startupEl = (Element) root.getElementsByTagName("startup-delay").item(0);
        val indexEl = (Element) root.getElementsByTagName("index-delay").item(0);

        return SchedulerSetting.builder()
                .executorService(createExecService(threadsEl))
                .startupDelay(XmlHelper.parseLong(startupEl))
                .indexDelay(XmlHelper.parseLong(indexEl))
                .allowIndex(true)
                .build();
    }

    /**
     * @param parent xml element to parse set of page settings from
     */
    private Collection<PageSetting> parsePageSettings(Element parent) {
        val nodes = parent.getElementsByTagName("page");
        val result = new ArrayList<PageSetting>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); ++i) {

            val entry = (Element) nodes.item(i);
            val analyzeParamsEl = ((Element) entry.getElementsByTagName("analyze-params").item(0));
            int minWeight = XmlHelper.parseInt(analyzeParamsEl, "min-weight");

            if (minWeight == 0) {
                minWeight = PageSetting.DEFAULT_WEIGHT;
            }

            val builder = PageSetting.builder()
                    .id(new PageID(XmlHelper.parseInt(entry, "id")))
                    .minWeight(minWeight)
                    .analyzeTemplates(parseAnalyzer(entry))
                    .formatTemplates(parseExtractor(entry))
                    .selectSettings(parseUrlExtractor(entry));

            result.add(builder.build());
        }

        return Collections.unmodifiableCollection(result);
    }

    /**
     * @param parent xml element to parse set of analyze settings
     */
    private Collection<AnalyzeTemplate> parseAnalyzer(Element parent) {

        val nodes = parent.getElementsByTagName("analyze");
        val result = new ArrayList<AnalyzeTemplate>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); ++i) {
            val entry = (Element) nodes.item(i);
            val weight = XmlHelper.parseInt(entry, "weight");
            val selector = entry.getTextContent();

            result.add(new AnalyzeTemplate(selector, weight));
        }

        return Collections.unmodifiableCollection(result);
    }

    /**
     * @param parent xml element to parse set of format settings
     */
    private Collection<FormatTemplate> parseExtractor(Element parent) {

        val nodes = ((Element) parent.getElementsByTagName("extract-params").item(0))
                .getElementsByTagName("extract");
        val result = new ArrayList<FormatTemplate>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); ++i) {
            val entry = (Element) nodes.item(i);
            val id = XmlHelper.parseInt(entry, "id");
            val selector = entry.getTextContent();

            result.add(new FormatTemplate(id, selector));
        }

        return Collections.unmodifiableCollection(result);
    }

    private Collection<UrlSelectSetting> parseUrlExtractor(Element parent) {

        val nodes = ((Element) parent.getElementsByTagName("url-params").item(0))
                .getElementsByTagName("extract");
        val result = new ArrayList<UrlSelectSetting>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); ++i) {
            val entry = (Element) nodes.item(i);
            val selector = entry.getAttribute("selector");
            val attr = entry.getAttribute("attr");

            result.add(new UrlSelectSetting(selector, attr));
        }

        return Collections.unmodifiableCollection(result);
    }

    /**
     * creates executive scheduled service by parsing xml element
     */
    private ScheduledExecutorService createExecService(Element element) {
        return element == null ? Executors.newSingleThreadScheduledExecutor() :
                Executors.newScheduledThreadPool(XmlHelper.parseInt(element));
    }

}