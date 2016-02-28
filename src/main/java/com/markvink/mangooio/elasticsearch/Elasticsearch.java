package com.markvink.mangooio.elasticsearch;

import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.markvink.mangooio.elasticsearch.client.ClientWrapper;
import com.markvink.mangooio.elasticsearch.client.DirectClientWrapper;
import com.markvink.mangooio.elasticsearch.client.NodeClientWrapper;
import com.markvink.mangooio.elasticsearch.client.TransportClientWrapper;
import com.markvink.mangooio.elasticsearch.document.Document;
import com.markvink.mangooio.elasticsearch.document.DocumentWithId;
import com.markvink.mangooio.elasticsearch.document.DocumentWithSource;

import io.mangoo.configuration.Config;

@Singleton
public class Elasticsearch implements ClientWrapper {

    private static final Logger LOG = LogManager.getLogger(Elasticsearch.class);

    private static final String CONFIG_PREFIX = "elasticsearch";

    private final ClientWrapper clientWrapper;

    /**
     * 
     * @param client
     *            the client to pass along
     */
    public Elasticsearch(Client client) {
        clientWrapper = new DirectClientWrapper(client);
    }

    /**
     *
     * @param config
     *            the config from Mangoo I/O
     */
    @Inject
    public Elasticsearch(Config config) {
        String host = config.getString(CONFIG_PREFIX.concat(".node.host"));
        if (StringUtils.isNoneBlank(host)) {
            clientWrapper = new TransportClientWrapper(config, CONFIG_PREFIX);
        } else {
            clientWrapper = new NodeClientWrapper(config, CONFIG_PREFIX);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.markvink.mangooio.elasticsearch.client.ClientWrapper#getClient()
     */
    @Override
    public Client getClient() {
        return clientWrapper.getClient();
    }

    /**
     * Adds the node.
     *
     * @param address
     *            the address of the Elasticsearch node
     * @param port
     *            the port of the Elasticsearch node
     */
    public void addNode(InetAddress address, int port) {
        if (clientWrapper instanceof TransportClientWrapper) {
            ((TransportClientWrapper) clientWrapper).addNode(address, port);
        }
    }

    /**
     * Removes the node.
     *
     * @param address
     *            the address of the Elasticsearch node
     * @param port
     *            the port of the Elasticsearch node
     */
    public void removeNode(InetAddress address, int port) {
        if (clientWrapper instanceof TransportClientWrapper) {
            ((TransportClientWrapper) clientWrapper).removeNode(address, port);
        }
    }

    /**
     * Creates an search index
     *
     * @param indexName
     *            the index name
     * @return the creates the index response
     */
    public CreateIndexResponse createIndex(String indexName) {
        IndicesExistsResponse indicesExistsResponse = getClient().admin().indices().exists(new IndicesExistsRequest(indexName)).actionGet();
        if (!indicesExistsResponse.isExists()) {
            CreateIndexRequestBuilder createIndexRequestBuilder = getClient().admin().indices().prepareCreate(indexName);
            return createIndexRequestBuilder.execute().actionGet();
        }
        return null;
    }

    /**
     * Index an document.
     *
     * @param indexName
     *            the index name
     * @param document
     *            the document
     * @return the index response
     */
    public IndexResponse indexDocument(String indexName, Document document) {
        IndexRequestBuilder indexRequestBuilder = getClient().prepareIndex(indexName, document.getDocumentType());
        if (document instanceof DocumentWithId) {
            indexRequestBuilder = getClient().prepareIndex(indexName, document.getDocumentType(),
                    ((DocumentWithId) document).getDocumentId());
        }

        if (document instanceof DocumentWithSource) {
            indexRequestBuilder.setSource(((DocumentWithSource) document).getDocumentSource());
        } else {
            try {
                indexRequestBuilder.setSource(MapperUtil.writeValueAsBytes(document));
            } catch (JsonProcessingException e) {
                LOG.error("Error converting source of document", e);
            }
        }

        return indexRequestBuilder.execute().actionGet();
    }

    /**
     * Gets an document based on his identifier
     *
     * @param indexName
     *            the index name
     * @param documentType
     *            the document type
     * @param documentId
     *            the document id
     * @return the document
     */
    public GetResponse getDocument(String indexName, String documentType, String documentId) {
        return getClient().prepareGet(indexName, documentType, documentId).execute().actionGet();
    }

    /**
     * Delete an document based on his identifier
     *
     * @param indexName
     *            the index name
     * @param documentType
     *            the document type
     * @param documentId
     *            the document id
     * @return the delete response
     */
    public DeleteResponse deleteDocument(String indexName, String documentType, String documentId) {
        return getClient().prepareDelete(indexName, documentType, documentId).execute().actionGet();
    }
}
