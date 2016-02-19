package com.markvink.mangooio.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.mangoo.configuration.Config;

@Singleton
public class Elasticsearch {
    private static final Logger LOG = LoggerFactory.getLogger(Elasticsearch.class);

    private static final String CONFIG_PREFIX = "elasticsearch";

    private TransportClient transportClient;

    @Inject
    public Elasticsearch(Config config) {

        Builder settings = Settings.settingsBuilder();
        settings.put("cluster.name", config.getString(CONFIG_PREFIX.concat(".cluster.name")));
        settings.put("client.transport.sniff", config.getBoolean(CONFIG_PREFIX.concat(".client.transport.sniff"), false));
        settings.put("client.transport.ignore_cluster_name",
                config.getBoolean(CONFIG_PREFIX.concat(".client.transport.ignore_cluster_name"), true));
        settings.put("client.transport.ping_timeout", config.getString(CONFIG_PREFIX.concat(".client.transport.ping_timeout"), "5s"));
        settings.put("client.transport.nodes_sampler_interval",
                config.getString(CONFIG_PREFIX.concat(".client.transport.nodes_sampler_interval"), "5s"));

        transportClient = TransportClient.builder().settings(settings).build();

        try {
            String host = config.getString(CONFIG_PREFIX.concat(".node.host"), "127.0.0.1");
            int port = config.getInt(CONFIG_PREFIX.concat(".node.port"), 9300);

            addNewNode(InetAddress.getByName(host), port);
        } catch (UnknownHostException e) {
            LOG.error("IP address of host could not be determined", e);
        }

        if (transportClient.connectedNodes().size() == 0) {
            LOG.warn("There are no active nodes available for the transport");
        }
    }

    public Client getClient() {
        return transportClient;
    }

    public void addNewNode(InetAddress address, int port) {
        transportClient.addTransportAddress(new InetSocketTransportAddress(address, port));
    }

    public void removeNode(InetAddress address, int port) {
        transportClient.removeTransportAddress(new InetSocketTransportAddress(address, port));
    }
}
