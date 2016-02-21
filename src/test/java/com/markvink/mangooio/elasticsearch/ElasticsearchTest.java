package com.markvink.mangooio.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Test;

public class ElasticsearchTest {

    private final Client mockClient = mock(Client.class, RETURNS_DEEP_STUBS);
    private final Elasticsearch elasticsearch = new Elasticsearch(mockClient);

    private final static String INDEX = "my-test-index";
    private final static String DOCUMENT_TYPE = "my-document-type";
    private final static String DOCUMENT_ID = "my-document-id";

    @Test
    public void getClientTest() {
        assertNotNull(elasticsearch.getClient());
        assertEquals(elasticsearch.getClient(), mockClient);
    }

    private IndicesAdminClient mockIndicesAdminClient() {
        AdminClient adminClient = mock(AdminClient.class);
        when(mockClient.admin()).thenReturn(adminClient);

        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        when(adminClient.indices()).thenReturn(indicesAdminClient);

        return indicesAdminClient;
    }

    @Test
    public void createIndexExistsTest() {
        /* Mock IndicesExistsRequest */
        IndicesAdminClient indicesAdminClient = mockIndicesAdminClient();

        @SuppressWarnings("unchecked")
        ActionFuture<IndicesExistsResponse> actionFuture = mock(ActionFuture.class);
        when(indicesAdminClient.exists(any(IndicesExistsRequest.class))).thenReturn(actionFuture);

        IndicesExistsResponse indicesExistsResponse = mock(IndicesExistsResponse.class);
        when(actionFuture.actionGet()).thenReturn(indicesExistsResponse);

        when(indicesExistsResponse.isExists()).thenReturn(true);

        /* Test */
        CreateIndexResponse response = elasticsearch.createIndex(INDEX);
        verify(indicesExistsResponse, times(1)).isExists();
        verify(indicesAdminClient, never()).prepareCreate(any(String.class));
        assertNull(response);
    }

    @Test
    public void createIndexNotExistsTest() {
        /* Mock IndicesExistsRequest */
        IndicesAdminClient indicesAdminClient = mockIndicesAdminClient();

        @SuppressWarnings("unchecked")
        ActionFuture<IndicesExistsResponse> actionFuture = mock(ActionFuture.class);
        when(indicesAdminClient.exists(any(IndicesExistsRequest.class))).thenReturn(actionFuture);

        IndicesExistsResponse indicesExistsResponse = mock(IndicesExistsResponse.class);
        when(actionFuture.actionGet()).thenReturn(indicesExistsResponse);

        when(indicesExistsResponse.isExists()).thenReturn(false);

        /* Mock CreateIndexRequest */
        CreateIndexRequestBuilder createIndexRequestBuilder = mock(CreateIndexRequestBuilder.class);
        when(indicesAdminClient.prepareCreate(any(String.class))).thenReturn(createIndexRequestBuilder);

        @SuppressWarnings("unchecked")
        ListenableActionFuture<CreateIndexResponse> listenableActionFuture = mock(ListenableActionFuture.class);
        when(createIndexRequestBuilder.execute()).thenReturn(listenableActionFuture);

        CreateIndexResponse createIndexResponse = mock(CreateIndexResponse.class);
        when(listenableActionFuture.actionGet()).thenReturn(createIndexResponse);

        /* Test */
        CreateIndexResponse response = elasticsearch.createIndex(INDEX);
        verify(indicesExistsResponse, times(1)).isExists();
        verify(indicesAdminClient, times(1)).prepareCreate(INDEX);
        assertEquals(createIndexResponse, response);
    }

    @Test
    public void getDocumentTest() {
        /* Mock GetResponse */
        GetRequestBuilder getRequestBuilder = mock(GetRequestBuilder.class);
        when(mockClient.prepareGet(any(String.class), any(String.class), any(String.class))).thenReturn(getRequestBuilder);

        @SuppressWarnings("unchecked")
        ListenableActionFuture<GetResponse> listenableActionFuture = mock(ListenableActionFuture.class);
        when(getRequestBuilder.execute()).thenReturn(listenableActionFuture);

        GetResponse getResponse = mock(GetResponse.class);
        when(listenableActionFuture.actionGet()).thenReturn(getResponse);

        /* Test */
        GetResponse response = elasticsearch.getDocument(INDEX, DOCUMENT_TYPE, DOCUMENT_ID);
        assertEquals(getResponse, response);
    }

    @Test
    public void deleteDocumentTest() {
        /* Mock DeleteResponse */
        DeleteRequestBuilder deleteRequestBuilder = mock(DeleteRequestBuilder.class);
        when(mockClient.prepareDelete(any(String.class), any(String.class), any(String.class))).thenReturn(deleteRequestBuilder);

        @SuppressWarnings("unchecked")
        ListenableActionFuture<DeleteResponse> listenableActionFuture = mock(ListenableActionFuture.class);
        when(deleteRequestBuilder.execute()).thenReturn(listenableActionFuture);

        DeleteResponse deleteResponse = mock(DeleteResponse.class);
        when(listenableActionFuture.actionGet()).thenReturn(deleteResponse);

        /* Test */
        DeleteResponse response = elasticsearch.deleteDocument(INDEX, DOCUMENT_TYPE, DOCUMENT_ID);
        assertEquals(deleteResponse, response);
    }
}
