package ua.com.papers.services.search;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.convertors.Fields.Publication;
import org.elasticsearch.common.Base64;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.util.*;

/**
 * Created by Andrii on 29.09.2016.
 */
@Component
public class SearchElasticImpl implements ISearchService {

    private Client client;
    @Autowired
    private IPublicationService publicationService;
    @Autowired
    private IPublisherService publisherService;

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

    @Override
    @Transactional
    public List<PublicationDTO> search(String query, int offset) {
        getClient();
        SearchHits searchHits = query(query, offset);
        return mapSearchHits(searchHits);
    }

    private SearchHits query(String query, int offset){
        QueryBuilder qb = QueryBuilders.multiMatchQuery(query)
                .field("title").boost(3)
                .field("annotation").boost(2)
                .field("authors").boost(2)
                .field("body.content")
                .fuzziness(Fuzziness.AUTO);

        SearchResponse sr = client.prepareSearch()
                .setQuery(qb)
                .setFrom(offset)
                .addFields("title", "authors", "annotation", "body.content")
                .addHighlightedField("title", 100, 1)
                .addHighlightedField("authors", 50, 1)
                .addHighlightedField("annotation", 100, 1)
                .addHighlightedField("body.content", 500, 1)
                .execute()
                .actionGet();
        return sr.getHits();
    }

    private List<PublicationDTO> mapSearchHits(SearchHits searchHits){
        List<PublicationDTO> publicationDTOs = new ArrayList<>(10);

        for (SearchHit hit: searchHits) {
            PublicationEntity publication = null;
            try {
                publication = publicationService.getPublicationById(Integer.valueOf(hit.getId()));
            } catch (NoSuchEntityException e) {
                e.printStackTrace();
            }
            if (publication == null) continue;

            PublicationDTO publicationDTO = new PublicationDTO();
            Map<String, SearchHitField> fields = hit.getFields();
            Map<String, HighlightField> highlightedFields = hit.getHighlightFields();

            publicationDTO.setId(publication.getId());
            if (highlightedFields.containsKey("title"))
                publicationDTO.setTitle(highlightedFields.get("title").getFragments()[0].toString());
            else publicationDTO.setTitle(fields.get("title").getValue().toString());

            if (highlightedFields.containsKey("authors"))
                publicationDTO.setAuthors(highlightedFields.get("authors").getFragments()[0].toString());
            else publicationDTO.setAuthors(fields.get("authors").getValue().toString());

            if (highlightedFields.containsKey("annotation"))
                publicationDTO.setAnnotation(highlightedFields.get("annotation").getFragments()[0].toString());
            else publicationDTO.setAnnotation(fields.get("annotation").getValue().toString());

            if (highlightedFields.containsKey("body.content"))
               publicationDTO.setBody(highlightedFields.get("body.content").getFragments()[0].toString());

            if (publication.getPublisher() != null) {
                publicationDTO.setPublisher(publication.getPublisher().getTitle());
            }
            publicationDTO.setType(publication.getType());
            publicationDTOs.add(publicationDTO);
        }
        return publicationDTOs;
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
