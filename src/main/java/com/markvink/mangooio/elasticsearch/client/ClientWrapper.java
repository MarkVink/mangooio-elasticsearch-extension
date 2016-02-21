package com.markvink.mangooio.elasticsearch.client;

import org.elasticsearch.client.Client;

public interface ClientWrapper {
    public Client getClient();
}
