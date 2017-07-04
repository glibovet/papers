package ua.com.papers.crawler.test;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ua.com.papers.crawler.core.creator.ICrawlerFactory;
import ua.com.papers.crawler.core.creator.ICreator;
import ua.com.papers.crawler.core.creator.xml.AbstractClasspathXmlCreator;
import ua.com.papers.crawler.core.creator.xml.XmlHelper;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.settings.*;

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

    public XmlCreator(String xsdPath, String filepath, ICrawlerFactory factory) {
        super(new File(getAbsolutePathForResource(filepath)),
                new File(getAbsolutePathForResource(xsdPath)), factory);
    }

    /**
     * get absolute path to file from relative
     *
     * @param relativePath - relative path to file stored in resources directory
     * @return absolute file path
     */
    private static String getAbsolutePathForResource(String relativePath) {
        ClassLoader loader = XmlCreator.class.getClassLoader();

        URL resource = loader.getResource(relativePath);
        if (resource == null) {
            return "";
        }
        return resource.getFile();
    }

    @Override
    protected Settings parseDocument(@NotNull Document document) {
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

        val processingThreadsEl = (Element) root.getElementsByTagName("processing-threads").item(0);
        val indexThreadsEl = (Element) root.getElementsByTagName("index-threads").item(0);
        val indexDelayEl = (Element) root.getElementsByTagName("index-delay").item(0);
        val processingDelayEl = (Element) root.getElementsByTagName("processing-delay").item(0);

        return SchedulerSetting.builder()
                .processingThreads(XmlHelper.parseInt(processingThreadsEl, 1))
                .indexThreads(XmlHelper.parseInt(indexThreadsEl, 1))
                .processingDelay(XmlHelper.parseLong(processingDelayEl))
                .indexDelay(XmlHelper.parseLong(indexDelayEl))
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
            val minWeight = XmlHelper.parseInt(analyzeParamsEl, "min-weight", PageSetting.DEFAULT_WEIGHT);

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

        val extractParams = (Element) parent.getElementsByTagName("extract-params").item(0);

        if (extractParams == null) return Collections.emptyList();

        val nodes = extractParams.getElementsByTagName("extract");
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

        val extractParams = (Element) parent.getElementsByTagName("url-params").item(0);

        if (extractParams == null) return Collections.emptyList();

        val nodes = extractParams.getElementsByTagName("extract");
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
