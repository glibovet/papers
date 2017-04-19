package ua.com.papers.services.elastic;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
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
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.storage.IStorageService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

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
            initializeIndex();
        return client.admin().indices()
                .prepareExists(elasticIndex)
                .execute().actionGet().isExists();
    }

    public Boolean indexDelete() throws ForbiddenException, ElasticSearchError {
        if(!sessionUtils.isUserWithRole(RolesEnum.admin))
            throw new ForbiddenException();
        if (client == null)
            initializeIndex();
        DeleteIndexResponse createResponse = client.admin().indices().prepareDelete(elasticIndex).execute()
                .actionGet();
        if (!createIndexIfNotExist()) {
            return false;
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
            initializeIndex();
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
            initializeIndex();
        if (!indexExist()){
            return createIndex();
        }

        List<PublicationEntity> entities = publicationService.getAllPublications();
        for (PublicationEntity entity : entities) {
            try {
                indexPublication(entity);
            } catch (ValidationException | NoSuchEntityException |ServiceErrorException e) {
                // nothing to do
            }
        }

        return true;
    }

    private boolean indexPublication(PublicationEntity publication) throws NoSuchEntityException, ServiceErrorException, ForbiddenException, ValidationException {
        XContentBuilder builder = buildPublicationJsonForIndexing(publication);
        if (builder == null)
            return false;
        IndexResponse response = client.prepareIndex(elasticIndex, papersType, String.valueOf(publication.getId()))
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
        if (publicationFile == null||publicationFile.length==0)
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
            initializeIndex();
        CreateIndexResponse createResponse = client.admin().indices().create(createIndexRequest(elasticIndex)).actionGet();
        try {
            PutMappingResponse putMappingResponse = client.admin().indices()
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

    private void initializeIndex(){
        if (client==null){
            try {
                Settings settings = Settings.settingsBuilder()
                        .put("cluster.name", elasticClusterName).build();
                client = TransportClient.builder()/*.settings(settings)*/.build()
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

    @Value("${elasticsearch.port.http}")
    private String elasticHttpPort;
}
