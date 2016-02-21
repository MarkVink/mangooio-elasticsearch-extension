package com.markvink.mangooio.elasticsearch.client;

import org.elasticsearch.client.Client;

public interface ClientWrapper {

    /**
     * Gets the client.
     *
     * @return the Elasticsearch client
     */
    public Client getClient();
}
