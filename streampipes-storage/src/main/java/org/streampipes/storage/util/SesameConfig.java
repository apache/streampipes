package org.streampipes.storage.util;


import org.streampipes.config.SpConfig;

public enum SesameConfig {
    INSTANCE;

    private SpConfig config;

    private final static String URI = "uri";
    private final static String REPOSITORY_ID = "test-6";

    SesameConfig() {
        config = SpConfig.getSpConfig("storage/sesame");

        config.register(URI, "http://backend:8030/openrdf-sesame", "URI for the sesame repository");
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
