package ua.com.papers.services.search;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Component;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.pojo.entities.PublicationEntity;

import org.elasticsearch.common.Base64;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.util.Date;

/**
 * Created by Andrii on 29.09.2016.
 */
@Component
public class SearchElasticImpl implements ISearchService {

    private Client client;

    @Override
    public void index(PublicationEntity publication) {

        getClient();
        if (client == null||publication==null)
            return;
        XContentBuilder builder = buildJSON(publication);
        if (builder == null)
            return;
        IndexResponse response = client.prepareIndex(elasticIndex, publicationType, String.valueOf(publication.getId()))
                .setSource(builder)
                .execute()
                .actionGet();

    }

    private void getClient(){
        if (client==null){
            try {
                Settings settings = Settings.settingsBuilder()
                        .put("cluster.name", elasticClasterName).build();
                client = TransportClient.builder().settings(settings).build()
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHost), elasticPort));
            } catch (UnknownHostException e) {}
        }
    }

    private XContentBuilder buildJSON(PublicationEntity publication){
        if (publication == null)
            return null;
        XContentBuilder builder = null;
        String fileContents = null;
        /*if (book.isHasEpub()){
            fileContents = formEpub(book);
        }
        try {
            builder = jsonBuilder()
                    .startObject()
                    .field("id", book.getId())
                    .field("isbn", book.getIsbn())
                    .field("name_uk", book.getName_uk())
                    .field("name_en", book.getName_en())
                    .field("name_ru", book.getName_ru())
                    .field("eighteenPlus", book.isEighteenPlus())
                    .field("yearOfPublication", book.getYearOfPublication())
                    .field("description_uk", book.getDescription_uk())
                    .field("description_ru", book.getDescription_ru())
                    .field("description_en", book.getDescription_en())
                    .field("status",book.getStatus());
            if (book.getBookToKeywords()!=null&&book.getBookToKeywords().size()>0){
                String keywords = "";
                for (BookKeywordEntity keyword:book.getBookToKeywords())
                    keywords +=keyword.getKeyword().getKeyword()+" ";
                builder.field("keywords",keywords);
            }
            if (book.getPublisher()!=null){
                builder.startObject("publisher")
                        .field("id", book.getPublisher().getId())
                        .field("nameUa", book.getPublisher().getNameUa())
                        .field("nameEn", book.getPublisher().getNameEn())
                        .field("nameRu", book.getPublisher().getNameRu())
                        .field("descriptionUa", book.getPublisher().getDescriptionUa())
                        .field("descriptionRu", book.getPublisher().getDescriptionRu())
                        .field("descriptionEn", book.getPublisher().getDescriptionEn());
                builder.endObject();
            }
            if (book.getBookToAuthor()!=null&&book.getBookToAuthor().size()>0){
                builder.startObject("authors");
                for (BookAuthorEntity author: book.getBookToAuthor()){
                    builder.startObject("author")
                            .field("id", author.getAuthor().getId())
                            .field("firstName_uk",author.getAuthor().getFirstName_uk())
                            .field("firstName_ru",author.getAuthor().getFirstName_ru())
                            .field("firstName_en",author.getAuthor().getFirstName_en())
                            .field("lastName_uk",author.getAuthor().getLastName_uk())
                            .field("lastName_ru",author.getAuthor().getLastName_ru())
                            .field("lastName_en",author.getAuthor().getLastName_en())
                            .field("description_uk",author.getAuthor().getDescription_uk())
                            .field("description_ru",author.getAuthor().getDescription_ru())
                            .field("description_en",author.getAuthor().getDescription_en())
                            .endObject();
                }
                builder.endObject();
            }
            if (fileContents!=null)
                builder.field("file",fileContents);
            builder.field("updated_at", new Date());
            builder.endObject();
        } catch (IOException e) {
        }*/
        return builder;
    }


    private String formPdf(PublicationEntity publication) {
        //try {
            //byte[] content = storageProvider.getPublication(publication.getId());
            //return Base64.encodeBytes(content);
            return null;
        /*} catch (ForbiddenException e) {
            return null;
        } catch (NoSuchEntityException e) {
            return null;
        } catch (ServiceException e) {
            return null;
        }*/
    }

    @Value("${elasticsearch.host}")
    private String elasticHost;

    @Value("${elasticsearch.port}")
    private Integer elasticPort;

    @Value("${elasticsearch.cluster_name}")
    private String elasticClasterName;

    @Value("${elasticsearch.index}")
    private String elasticIndex;

    @Value("${elasticsearch.publication_type}")
    private String publicationType;
}
