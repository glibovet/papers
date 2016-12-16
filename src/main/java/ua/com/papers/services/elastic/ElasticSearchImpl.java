package ua.com.papers.services.elastic;

import com.google.gson.JsonObject;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.jglue.fluentjson.JsonBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ua.com.papers.exceptions.service_error.ElasticSearchError;
import ua.com.papers.exceptions.service_error.ForbiddenException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.utils.SessionUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import static org.elasticsearch.client.Requests.createIndexRequest;

/**
 * Created by Andrii on 12.11.2016.
 */
@Service
public class ElasticSearchImpl implements IElasticSearch{

    private Client client;

    @Autowired
    private SessionUtils sessionUtils;

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

    public Boolean indexDelete() throws ForbiddenException {
        if(!sessionUtils.isUserWithRole(RolesEnum.admin))
            throw new ForbiddenException();
        if (client == null)
            initializeIndex();
        DeleteIndexResponse createResponse = client.admin().indices().prepareDelete(elasticIndex).execute()
                .actionGet();
        return true;
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
                        .put("cluster.name", elasticClasterName).build();
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
    private String elasticClasterName;

    @Value("${elasticsearch.index}")
    private String elasticIndex;

    @Value("${elasticsearch.publication_type}")
    private String papersType;

    @Value("${elasticsearch.port.http}")
    private String elasticHttpPort;
}
