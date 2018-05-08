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
