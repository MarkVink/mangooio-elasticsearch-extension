package com.markvink.mangooio.elasticsearch.client;

import org.elasticsearch.client.Client;

public class DirectClientWrapper implements ClientWrapper {

    private Client client;

    /**
     * Instantiates a new direct client wrapper.
     *
     * @param client
     *            the client to pass along
     */
    public DirectClientWrapper(Client client) {
        this.client = client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.markvink.mangooio.elasticsearch.client.ClientWrapper#getClient()
     */
    @Override
    public Client getClient() {
        return client;
    }
}
