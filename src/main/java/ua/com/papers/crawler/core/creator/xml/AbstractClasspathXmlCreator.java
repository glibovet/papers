package ua.com.papers.crawler.core.creator.xml;

import com.google.common.base.Preconditions;
import lombok.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ua.com.papers.crawler.core.creator.ICrawlerFactory;
import ua.com.papers.crawler.core.creator.ICreator;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;
import ua.com.papers.crawler.settings.Settings;

import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 *     XML-based abstract creator which
 *     partially implements {@linkplain ICreator}
 * </p>
 * Created by Максим on 1/16/2017.
 */
@Data
public abstract class AbstractClasspathXmlCreator implements ICreator {

    private final File file;
    private final ICrawlerFactory factory;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Settings cachedSettings;

    @Getter(value = AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PROTECTED)
    private boolean isCacheEnabled;


    public AbstractClasspathXmlCreator(@NotNull File file, @NotNull File xsd, @NotNull ICrawlerFactory factory) {
        AbstractClasspathXmlCreator.checkFile(file);
        AbstractClasspathXmlCreator.checkXmlValid(file, xsd);
        Preconditions.checkNotNull(factory, "crawler factory == null");

        this.file = file;
        this.factory = factory;
        this.isCacheEnabled = true;
    }

    @Override
    public final ICrawlerManager create() {
        // parsing xml file is quite expensive
        // and long-running operation, so that's
        // why caching settings is enabled by default,
        // however, such behaviour may be disabled
        if (isCacheEnabled() && cachedSettings != null) {
            return factory.create(cachedSettings);
        }

        final Document document;

        try {
            val docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            document = docBuilder.parse(getFile());
        } catch (final Exception e) {
            // cannot create document, re-throw exception
            // to an upper level
            throw new RuntimeException(e);
        }

        if (isCacheEnabled()) {
            return factory.create(cachedSettings = parseDocument(document));
        }

        cachedSettings = null;
        return factory.create(parseDocument(document));
    }

    protected abstract Settings parseDocument(@NotNull Document doc);

    protected static void checkFile(File file) {
        Preconditions.checkNotNull(file, "file == null");
        Preconditions.checkArgument(file.exists() && file.isFile(),
                String.format("File %s doesn't exist or points to a directory", file));
    }

    protected static void checkXmlValid(File target, File xsd) {

        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            final Schema schema = schemaFactory.newSchema(xsd);
            final Validator validator = schema.newValidator();
            final Source source = new StreamSource(target);

            validator.validate(source);

        } catch (final SAXException e) {
            throw new RuntimeException(
                    String.format("Invalid file %s, xsd %s", target, xsd), e);
        } catch (final IOException e) {
            throw new RuntimeException(
                    String.format("An error occurred while reading file %s, xsd %s", target, xsd), e);
        }
    }

}
