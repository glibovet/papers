package ua.com.papers.crawler.core.creator.xml;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.val;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ua.com.papers.crawler.core.creator.ICreator;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;
import ua.com.papers.crawler.settings.Settings;
import ua.com.papers.crawler.util.ICrawlerFactory;

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
 * Created by Максим on 1/16/2017.
 */
@Data
public abstract class AbstractClasspathXmlCreator implements ICreator {

    private final File file;
    private final ICrawlerFactory factory;

    public AbstractClasspathXmlCreator(@NotNull File file, @NotNull File xsd, @NotNull ICrawlerFactory factory) {
        AbstractClasspathXmlCreator.checkFile(file);
        AbstractClasspathXmlCreator.checkXmlValid(file, xsd);
        Preconditions.checkNotNull(factory, "crawler factory == null");

        this.file = file;
        this.factory = factory;
    }

    @Override
    public final ICrawlerManager create() {

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

        return factory.create(parseFile(document));
    }

    protected abstract Settings parseFile(@NotNull Document doc);

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
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("An error occurred while reading file %s, xsd %s", target, xsd), e);
        }
    }

}