package org.streampipes.storage.util;

import org.streampipes.commons.SpConfigChangeCallback;
import org.streampipes.commons.config.SpConfig;

public enum CouchDbConfig {

    INSTANCE;


    private class TestOnChange implements SpConfigChangeCallback {

        @Override
        public void onChange() {
            System.out.println("Config changed Yeahh!!");

        }
    }


    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String PROTOCOL = "protocol";

    CouchDbConfig() {
//        config = SpConfig.getSpConfig("storage/couchdb", new TestOnChange());
        config = SpConfig.getSpConfig("storage/couchdb");
//
        config.register(HOST, "couchdb", "Hostname for the couch db service");
        config.register(PORT, 5984, "Port for the couch db service");
        config.register(PROTOCOL, "http", "Protocol the couch db service");

    }

    public String getHost() {
        return config.getString(HOST);
    }

    public int getPort() {
        return config.getInteger(PORT);
    }

    public String getProtocol() {
        return config.getString(PROTOCOL);
    }
}
