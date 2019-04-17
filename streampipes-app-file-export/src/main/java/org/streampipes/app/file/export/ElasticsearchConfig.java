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

package org.streampipes.app.file.export;

import org.streampipes.config.SpConfig;

public enum ElasticsearchConfig {
    INSTANCE;

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String PROTOCOL = "protocol";
    private final static String DATA_LOCATION = "data_location";

    ElasticsearchConfig() {
        config = SpConfig.getSpConfig("storage/elasticsearch");

        config.register(HOST, "elasticsearch", "Hostname for the elasticsearch service");
        config.register(PORT, 9200, "Port for the elasticsearch service");
        config.register(PROTOCOL, "http", "Protocol the elasticsearch service");
        config.register(DATA_LOCATION,"/home/user/", "Folder that stores all the created data blobs");
    }

    public String getElasticsearchHost() {
        return config.getString(HOST);
    }

    public Integer getElasticsearchPort() {
        return config.getInteger(PORT);
    }

    public String getElasticsearchURL() {
        return getElasticsearchProtocol()+ "://" + getElasticsearchHost() + ":" + getElasticsearchPort();
    }

    public String getElasticsearchProtocol() {
        return config.getString(PROTOCOL);
    }

    public String getDataLocation() {
        return config.getString(DATA_LOCATION);
    }
}
