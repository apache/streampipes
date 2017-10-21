package org.streampipes.storage.util;

import org.streampipes.config.SpConfig;

public enum ElasticsearchConfig {
    INSTANCE;

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String PROTOCOL = "protocol";

    ElasticsearchConfig() {
        config = SpConfig.getSpConfig("storage/elasticsearch");

        config.register(HOST, "elasticsearch", "Hostname for the elasticsearch service");
        config.register(PORT, "9200", "Port for the elasticsearch service");
        config.register(PROTOCOL, "http", "Protocol the elasticsearch service");
    }

    public String getElasticsearchHost() {
        return config.getString(HOST);
    }

    public String getElasticsearchPort() {
        return config.getString(PORT);
    }

    public String getElasticsearchURL() {
        return getElasticsearchProtocol()+ "://" + getElasticsearchHost() + ":" + getElasticsearchPort();
    }

    public String getElasticsearchProtocol() {
        return config.getString(PROTOCOL);
    }

}
