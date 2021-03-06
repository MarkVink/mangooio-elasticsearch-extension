package com.markvink.mangooio.elasticsearch.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import io.mangoo.configuration.Config;

public class TransportClientWrapper implements ClientWrapper {

    private static final Logger LOG = LogManager.getLogger(TransportClientWrapper.class);

    private final TransportClient transportClient;

    /**
     * Instantiates a new transport client wrapper.
     *
     * @param config
     *            the Mangoo I/O config
     * @param prefix
     *            the prefix for the config keys
     */
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

        transportClient = createClient(settings.build(), host, port);
    }

    /**
     * Creates the client.
     *
     * @param settings
     *            the client settings
     * @param host
     *            the host of the Elasticsearch node
     * @param port
     *            the port of the Elasticsearch node
     * @return the client
     */
    private TransportClient createClient(Settings settings, String host, int port) {
        TransportClient transportClient = TransportClient.builder().settings(settings).build();

        try {
            InetSocketTransportAddress address = new InetSocketTransportAddress(InetAddress.getByName(host), port);
            transportClient.addTransportAddress(address);
        } catch (UnknownHostException e) {
            LOG.error("IP address of host could not be determined", e);
        }

        if (transportClient.connectedNodes().size() == 0) {
            LOG.warn("There are no active nodes available for the transport");
        }

        return transportClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.markvink.mangooio.elasticsearch.client.ClientWrapper#getClient()
     */
    @Override
    public Client getClient() {
        return transportClient;
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
        LOG.info("Add node {}:{}", address, port);
        transportClient.addTransportAddress(new InetSocketTransportAddress(address, port));
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
        LOG.info("Remove node {}:{}", address, port);
        transportClient.removeTransportAddress(new InetSocketTransportAddress(address, port));
    }
}
