package com.markvink.mangooio.elasticsearch.client;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;

import io.mangoo.configuration.Config;

public class NodeClientWrapper implements ClientWrapper {

    private final Client nodeClient;

    /**
     * Instantiates a new node client wrapper.
     *
     * @param config
     *            the Mangoo I/O config
     * @param prefix
     *            the prefix for the config keys
     */
    public NodeClientWrapper(Config config, String prefix) {
        Builder settings = Settings.settingsBuilder();
        settings.put("cluster.name", config.getString(prefix.concat(".cluster.name")));
        settings.put("path.home", config.getString(prefix.concat(".path.home")));
        settings.put("index.store.type", config.getString(prefix.concat(".index.store.type")));
        settings.put("http.enabled", config.getBoolean(prefix.concat(".http.enabled"), false));

        nodeClient = createClient(settings.build());
    }

    /**
     * Creates the client.
     *
     * @param settings
     *            the client settings
     * @return the client
     */
    private Client createClient(Settings settings) {
        Node node = nodeBuilder().settings(settings).node();
        return node.client();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.markvink.mangooio.elasticsearch.client.ClientWrapper#getClient()
     */
    @Override
    public Client getClient() {
        return nodeClient;
    }
}
