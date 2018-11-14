/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.couchdb.utils;

import org.streampipes.config.SpConfig;
import org.streampipes.config.SpConfigChangeCallback;

public enum CouchDbConfig {

    INSTANCE;

    private SpConfig config;
    private final static String COUCHDB_HOST = "SP_COUCHDB_HOST";
    private final static String COUCHDB_PORT = "SP_COUCHDB_PORT";
    private final static String PROTOCOL = "PROTOCOL";

    CouchDbConfig() {
//        config = SpConfig.getSpConfig("storage/couchdb", new TestOnChange());
        config = SpConfig.getSpConfig("storage/couchdb");
//
        config.register(COUCHDB_HOST, "couchdb", "Hostname for the couch db service");
        config.register(COUCHDB_PORT, 5984, "Port for the couch db service");
        config.register(PROTOCOL, "http", "Protocol the couch db service");

    }

    private class TestOnChange implements SpConfigChangeCallback {

        @Override
        public void onChange() {
            System.out.println("Config changed Yeahh!!");

        }
    }

    public String getHost() {
        return config.getString(COUCHDB_HOST);
    }

    public int getPort() {
        return config.getInteger(COUCHDB_PORT);
    }

    public String getProtocol() {
        return config.getString(PROTOCOL);
    }

    public void setHost(String host) {
        config.setString(COUCHDB_HOST, host);
    }
}
