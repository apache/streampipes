package org.streampipes.storage.rdf4j.util;


import org.streampipes.config.SpConfig;

public enum SesameConfig {
    INSTANCE;

    private SpConfig config;

    private final static String URI = "uri";
    private final static String REPOSITORY_ID = "streampipes";

    SesameConfig() {
        config = SpConfig.getSpConfig("storage/sesame");

        config.register(URI, "http://backend:8030/rdf4j-server", "URI for the sesame repository");
    }

    public String getUri() {
        return config.getString(URI);
    }

    public String getRepositoryId() {
        return REPOSITORY_ID;
    }

    public void setUri(String value) {
        config.setString(URI, value);
    }

}
