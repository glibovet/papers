package ua.com.papers.services.elastic;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ElasticSearchError;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.storage.IStorageService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.client.Requests.createIndexRequest;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by Andrii on 12.11.2016.
 */
@Service
public class ElasticSearchImpl implements IElasticSearch{

    private Client client;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private IPublicationService publicationService;

    @Autowired
    private IStorageService storageService;

    public ElasticSearchImpl() {
        this.DEFAULT_HIGHLIGHTER = new HighlightBuilder()
                .field("title", 100, 1)
                .field("authors", 50, 1)
                .field("annotation", 100, 1)
                .field("body.content", 500, 1)
                .preTags("<em data-highlight><b>")
                .postTags("</b></em>");
    }

    @Override
    public Boolean createIndexIfNotExist() throws ForbiddenException, ElasticSearchError {
        if(!sessionUtils.isUserWithRole(RolesEnum.admin))
            throw new ForbiddenException();
        if (!indexExist()){
            return createIndex();
        }
        return true;
    }

    public Boolean indexExist() throws ElasticSearchError {
        if (client == null)
            initializeClient();
        return client
                .admin()
                .indices()
                .prepareExists(elasticIndex)
                .execute()
                .actionGet()
                .isExists();
    }

    public Boolean indexDelete() throws ForbiddenException, ElasticSearchError, NoSuchEntityException {
        if(!sessionUtils.isUserWithRole(RolesEnum.admin))
            throw new ForbiddenException();
        if (client == null)
            initializeClient();
        try {
            client
                    .admin()
                    .indices()
                    .prepareDelete(elasticIndex)
                    .execute()
                    .actionGet();
        } catch (IndexNotFoundException e) {
            throw new ElasticSearchError("індекс все ще не створений");
        }

        publicationService.removePublicationsFromIndex();

        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean indexPublication(int id) throws ForbiddenException, NoSuchEntityException, ServiceErrorException, ValidationException, ElasticSearchError {
        if(!sessionUtils.isUserWithRole(RolesEnum.admin))
            throw new ForbiddenException();
        if (client == null)
            initializeClient();
        if (!indexExist()){
            return createIndex();
        }

        PublicationEntity publication = publicationService.getPublicationById(id);
        return indexPublication(publication);
    }

    @Override
    @Transactional
    public boolean indexAll() throws ForbiddenException, ElasticSearchError {
        if(!sessionUtils.isUserWithRole(RolesEnum.admin))
            throw new ForbiddenException();
        if (client == null)
            initializeClient();
        if (!indexExist()){
            return createIndex();
        }

        List<PublicationEntity> entities = publicationService.getAllPublications();
        for (PublicationEntity entity : entities) {
            try {
                indexPublication(entity);
            } catch (ValidationException | NoSuchEntityException | ServiceErrorException e) {
                // nothing to do
            }
        }

        return true;
    }

    @Override
    @Transactional
    public List<PublicationDTO> search(String query, int offset) {
        if (client == null)
            initializeClient();
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

        SearchRequestBuilder sb = client.prepareSearch()
                .setQuery(qb)
                .setFrom(offset)
                .addFields("title", "authors", "annotation", "body.content");
        sb.internalBuilder().highlight(DEFAULT_HIGHLIGHTER);

        SearchResponse response = sb.execute().actionGet();

        return response.getHits();
    }

    private List<PublicationDTO> mapSearchHits(SearchHits searchHits){
        List<PublicationDTO> publicationDTOs = new ArrayList<>(10);

        for (SearchHit hit : searchHits) {
            PublicationEntity publication;
            try {
                publication = publicationService.getPublicationById(Integer.valueOf(hit.getId()));
            } catch (NoSuchEntityException e) {
                continue;
            }

            PublicationDTO publicationDTO = new PublicationDTO();
            Map<String, SearchHitField> fields = hit.getFields();
            Map<String, HighlightField> highlightedFields = hit.getHighlightFields();

            publicationDTO.setId(publication.getId());
            publicationDTO.setTitle(getFieldValue(
                    fields, highlightedFields, "title"
            ));
            publicationDTO.setAuthors(getFieldValue(
                    fields, highlightedFields, "authors"
            ));
            publicationDTO.setAnnotation(getFieldValue(
                    fields, highlightedFields, "annotation"
            ));
            publicationDTO.setBody(getFieldValue(
                    fields, highlightedFields, "body.content"
            ));
            if (publication.getPublisher() != null) {
                publicationDTO.setPublisher(publication.getPublisher().getTitle());
            }
            publicationDTO.setType(publication.getType());

            publicationDTOs.add(publicationDTO);
        }
        return publicationDTOs;
    }

    private String getFieldValue(Map<String, SearchHitField> fields, Map<String, HighlightField> highlightedFields, String fieldName) {
        if (highlightedFields.containsKey(fieldName)) {
            return highlightedFields.get(fieldName).getFragments()[0].toString();
        } else if (fields.containsKey(fieldName)) {
            return fields.get(fieldName).getValue();
        }
        return "";
    }

    private boolean indexPublication(PublicationEntity publication) throws NoSuchEntityException, ServiceErrorException, ForbiddenException, ValidationException {
        XContentBuilder builder = buildPublicationJsonForIndexing(publication);
        if (builder == null)
            return false;
        client
                .prepareIndex(elasticIndex, papersType, String.valueOf(publication.getId()))
                .setSource(builder)
                .execute()
                .actionGet();
        publication.setInIndex(true);
        publicationService.updatePublication(publication);
        return true;
    }

    private XContentBuilder buildPublicationJsonForIndexing(PublicationEntity publication) throws NoSuchEntityException, ForbiddenException, ServiceErrorException {
        if (publication == null)
            return null;
        byte[] publicationFile = storageService.getPaperAsByteArray(publication);
        if (publicationFile == null || publicationFile.length == 0)
            return null;
        XContentBuilder builder = null;
        String authors = "";
        if (publication.getAuthors()!=null){
            for (AuthorMasterEntity author:publication.getAuthors()){
                authors+=author.getLastName()+" "+author.getInitials()+" ";
            }
        }
        try {
            builder = jsonBuilder()
                    .startObject()
                    .field("id", publication.getId())
                    .field("title", publication.getTitle())
                    .field("annotation", publication.getAnnotation())
                    .field("authors",authors)
                    .field("body", Base64.encodeBytes(publicationFile));
            builder.endObject();
        } catch (IOException e) {
        }
        return builder;
    }

    private Boolean createIndex() {
        if (client == null)
            initializeClient();
        client
                .admin()
                .indices()
                .create(createIndexRequest(elasticIndex))
                .actionGet();
        try {
            client
                    .admin()
                    .indices()
                    .preparePutMapping(elasticIndex)
                    .setType(papersType)
                    .setSource(buildMapping())
                    .execute().actionGet();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public XContentBuilder buildMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().
                startObject().
                startObject(papersType).
                startObject("properties").
                    startObject("id").
                        field("type", "integer").
                        field("store", "yes").
                    endObject().
                    startObject("title").
                        field("type", "string").
                        field("store", "yes").
                    endObject().
                    startObject("annotation").
                        field("type", "string").
                        field("store", "yes").
                    endObject().
                    startObject("authors").
                        field("type", "string").
                        field("store", "yes").
                    endObject().
                    startObject("body").
                        field("type", "attachment").
                        startObject("fields").
                            startObject("content").
                                field("type","string").
                                field("term_vector","with_positions_offsets").
                                field("store","yes").
                            endObject().
                        endObject().
                    endObject().
                endObject().
                endObject().
                endObject();
        return builder;
    }

    private void initializeClient(){
        if (client == null){
            try {
                Settings settings = Settings.settingsBuilder()
                        .put("cluster.name", elasticClusterName)
                        .put("client.transport.sniff",true)
                        .build();
                client = TransportClient.builder().settings(settings).build()
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHost), elasticPort));
            } catch (UnknownHostException e) {}
        }
    }

    @Value("${elasticsearch.host}")
    private String elasticHost;

    @Value("${elasticsearch.port}")
    private Integer elasticPort;

    @Value("${elasticsearch.cluster_name}")
    private String elasticClusterName;

    @Value("${elasticsearch.index}")
    private String elasticIndex;

    @Value("${elasticsearch.publication_type}")
    private String papersType;

    private final HighlightBuilder DEFAULT_HIGHLIGHTER;
}
