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
import org.apache.streampipes.model.node.NodeInfo;
import org.apache.streampipes.model.node.Node;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public enum NodeControllerConfig {
    INSTANCE;

    private SpConfig config;
    private final static String node_service_id = "node/";
    private Node node = parseNodeConfig(System.getenv("NODE_INFO_YAML_FILE"));

    NodeControllerConfig() {
        config = SpConfig.getSpConfig(node_service_id + node.getNodeInfo().getNodeId());

        config.register(ConfigKeys.NODE_ID_KEY, node.getNodeInfo().getNodeId(), "node id");
        config.register(ConfigKeys.NODE_SERVICE_PORT_KEY, node.getNodeInfo().getNodeMetadata().getNodePort(), "node port");
        config.register(ConfigKeys.NODE_METADATA_HOSTNAME_KEY, node.getNodeInfo().getNodeMetadata().getNodeName(), "node host name");
        config.register(ConfigKeys.NODE_METADATA_LOCATION_KEY, node.getNodeInfo().getNodeMetadata().getNodeLocation(), "node location");

    }

    public String getNodeID() {
        return node.getNodeInfo().getNodeId();
    }

    public int getNodeServicePort(){
        return node.getNodeInfo().getNodeMetadata().getNodePort();
    }

    public String getNodeHostName(){
        return node.getNodeInfo().getNodeMetadata().getNodeName();
    }

    public Node getNodeInfoFromConfig(){
        return node;
    }

    private static Node parseNodeConfig(String s) {
        Yaml yaml = new Yaml(new Constructor(Node.class));
        InputStream inputStream = NodeInfo.class
                .getClassLoader()
                .getResourceAsStream(s);
        return yaml.load(inputStream);
    }

}
