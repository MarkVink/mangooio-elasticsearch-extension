package com.markvink.mangooio.elasticsearch.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mangoo.configuration.Config;

public class TransportClientWrapper implements ClientWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(TransportClientWrapper.class);

    private TransportClient transportClient;

    public TransportClientWrapper(Config config, String prefix) {
        Builder settings = Settings.settingsBuilder();
        settings.put("cluster.name", config.getString(prefix.concat(".cluster.name")));
        settings.put("client.transport.sniff", config.getBoolean(prefix.concat(".client.transport.sniff"), false));
        settings.put("client.transport.ignore_cluster_name",
                config.getBoolean(prefix.concat(".client.transport.ignore_cluster_name"), true));
        settings.put("client.transport.ping_timeout", config.getString(prefix.concat(".client.transport.ping_timeout"), "5s"));
        settings.put("client.transport.nodes_sampler_interval",
                config.getString(prefix.concat(".client.transport.nodes_sampler_interval"), "5s"));

        String host = config.getString(prefix.concat(".node.host"));
        int port = config.getInt(prefix.concat(".node.port"), 9300);

        createClient(settings.build(), host, port);
    }

    private void createClient(Settings settings, String host, int port) {
        transportClient = TransportClient.builder().settings(settings).build();

        try {
            addNewNode(InetAddress.getByName(host), port);
        } catch (UnknownHostException e) {
            LOG.error("IP address of host could not be determined", e);
        }

        if (transportClient.connectedNodes().size() == 0) {
            LOG.warn("There are no active nodes available for the transport");
        }
    }

    @Override
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
