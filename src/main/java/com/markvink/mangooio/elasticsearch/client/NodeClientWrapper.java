package com.markvink.mangooio.elasticsearch.client;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;

import io.mangoo.configuration.Config;

public class NodeClientWrapper implements ClientWrapper {

    private Client nodeClient;

    public NodeClientWrapper(Config config, String prefix) {
        Builder settings = Settings.settingsBuilder();
        settings.put("cluster.name", config.getString(prefix.concat(".cluster.name")));
        settings.put("http.enabled", config.getBoolean(prefix.concat(".http.enabled"), false));

        createClient(settings.build());
    }

    private void createClient(Settings settings) {
        Node node = nodeBuilder().node();
        nodeClient = node.client();
    }

    @Override
    public Client getClient() {
        return nodeClient;
    }
}
