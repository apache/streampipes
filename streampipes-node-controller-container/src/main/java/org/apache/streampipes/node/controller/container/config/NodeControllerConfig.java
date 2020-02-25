package org.apache.streampipes.node.controller.container.config;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import org.apache.streampipes.config.SpConfig;

public enum NodeControllerConfig {
    INSTANCE;

    private SpConfig config;

    private final static String node_service_id = "node/";
//    private UUID uuid = UUID.randomUUID();
//    String nodeUUID = uuid.toString();

    NodeControllerConfig() {
        config = SpConfig.getSpConfig(node_service_id + getEnvVariable(ConfigKeys.NODE_HOSTNAME_KEY));

        config.register(ConfigKeys.NODE_ID_KEY, getEnvVariable(ConfigKeys.NODE_ID_KEY), "node controller id");
        config.register(ConfigKeys.NODE_SERVICE_NAME_KEY, getEnvVariable(ConfigKeys.NODE_SERVICE_NAME_KEY), "node service name");
        config.register(ConfigKeys.NODE_PORT_KEY, getEnvVariable(ConfigKeys.NODE_PORT_KEY), "node controller port");
        config.register(ConfigKeys.NODE_HOSTNAME_KEY, getEnvVariable(ConfigKeys.NODE_HOSTNAME_KEY), "node hostname");
    }

    private String getEnvVariable(String key) {
        return System.getenv(key);
    }

    public String getNodeControllerURL() { return "http://" + getNodeHostName() + ":" + getNodePort(); }

    public String getNodeControllerServiceName() { return config.getString(ConfigKeys.NODE_SERVICE_NAME_KEY); }

    public String getNodeID() {
        return config.getString(ConfigKeys.NODE_ID_KEY);
    }

    public Integer getNodePort(){
        return config.getInteger(ConfigKeys.NODE_PORT_KEY);
    }

    public String getNodeHostName(){
        return config.getString(ConfigKeys.NODE_HOSTNAME_KEY);
    }


}
