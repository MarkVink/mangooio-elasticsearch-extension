package com.markvink.mangooio.elasticsearch;

import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.markvink.mangooio.elasticsearch.client.ClientWrapper;
import com.markvink.mangooio.elasticsearch.client.NodeClientWrapper;
import com.markvink.mangooio.elasticsearch.client.TransportClientWrapper;
import com.markvink.mangooio.elasticsearch.document.DocumentWithId;
import com.markvink.mangooio.elasticsearch.document.DocumentWithoutId;

import io.mangoo.configuration.Config;

@Singleton
public class Elasticsearch implements ClientWrapper {
    private static final String CONFIG_PREFIX = "elasticsearch";

    private ClientWrapper clientWrapper;

    @Inject
    public Elasticsearch(Config config) {
        String host = config.getString(CONFIG_PREFIX.concat(".node.host"));
        if (StringUtils.isNoneBlank(host)) {
            clientWrapper = new TransportClientWrapper(config, CONFIG_PREFIX);
        } else {
            clientWrapper = new NodeClientWrapper(config, CONFIG_PREFIX);
        }
    }

    @Override
    public Client getClient() {
        return clientWrapper.getClient();
    }

    public void addNewNode(InetAddress address, int port) {
        if (clientWrapper instanceof TransportClientWrapper) {
            ((TransportClientWrapper) clientWrapper).addNewNode(address, port);
        }
    }

    public void removeNode(InetAddress address, int port) {
        if (clientWrapper instanceof TransportClientWrapper) {
            ((TransportClientWrapper) clientWrapper).removeNode(address, port);
        }
    }

    public CreateIndexResponse createIndex(String indexName) {
        CreateIndexRequestBuilder createIndexRequestBuilder = getClient().admin().indices().prepareCreate(indexName);
        return createIndexRequestBuilder.execute().actionGet();
    }

    public IndexResponse indexDocument(String indexName, DocumentWithoutId document) {
        IndexRequestBuilder indexRequestBuilder = getClient().prepareIndex(indexName, document.getDocumentType());
        indexRequestBuilder.setSource(document.getDocumentContent());
        return indexRequestBuilder.execute().actionGet();
    }

    public IndexResponse indexDocument(String indexName, DocumentWithId document) {
        IndexRequestBuilder indexRequestBuilder = getClient().prepareIndex(indexName, document.getDocumentType(), document.getDocumentId());
        indexRequestBuilder.setSource(document.getDocumentContent());
        return indexRequestBuilder.execute().actionGet();
    }

    public GetResponse getDocument(String indexName, String documentType, String documentId) {
        GetRequestBuilder getRequestBuilder = getClient().prepareGet(indexName, documentType, documentId);
        return getRequestBuilder.execute().actionGet();
    }

    public DeleteResponse deleteDocument(String indexName, String documentType, String documentId) {
        DeleteRequestBuilder deleteRequestBuilder = getClient().prepareDelete(indexName, documentType, documentId);
        return deleteRequestBuilder.execute().actionGet();
    }
}
