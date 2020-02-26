package org.apache.streampipes.node.controller.container.description;/*
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

import org.apache.streampipes.model.node.*;
import org.apache.streampipes.model.node.capabilities.hardware.Hardware;
import org.apache.streampipes.model.node.capabilities.interfaces.Interfaces;
import org.apache.streampipes.model.node.capabilities.software.Software;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;

import java.util.List;

public class NodeInfoDescription {

    private final static Node node = NodeControllerConfig.INSTANCE.getNodeInfoFromConfig();

    public static NodeInfo retrieveNodeInfo(){
        return NodeInfoBuilder.create(nodeId())
                .withNodeNameAndPort(nodeName(), nodePort())
                .withNodeLocation(nodeLocation())
                .withNodeDescription(nodeDescription())
                .withNodeCapabilities(nodeHardware(), nodeSoftware(), nodeInterfaces())
                .withJmsTransportProtocol(nodeBrokerHost(), nodeBrokerPort())
                .withSupportedPipelineElements(supportedPipelineElements())
                .build();
    }

    private static String nodeId() {
        return getNodeInfo().getNodeId();
    }

    private static String nodeName() {
        return getNodeMetadata().getNodeName();
    }

    private static int nodePort() {
        return getNodeMetadata().getNodePort();
    }

    private static String nodeLocation() {
        return getNodeMetadata().getNodeLocation();
    }

    private static String nodeDescription() {
        return getNodeMetadata().getNodeDescription();
    }

    private static String nodeBrokerHost() {
        return getNodeBrokerInfo().getHost();
    }

    private static int nodeBrokerPort() {
        return getNodeBrokerInfo().getPort();
    }

    private static Hardware nodeHardware() {
        return getNodeCapabilities().getHardware();
    }

    private static Software nodeSoftware() {
        return getNodeCapabilities().getSoftware();
    }

    private static List<Interfaces> nodeInterfaces() {
        return getNodeCapabilities().getInterfaces();
    }

    private static List<String> supportedPipelineElements() {
        return getNodeInfo().getSupportedPipelineElementAppIds();
    }

    private static NodeInfo getNodeInfo() {
        return node.getNodeInfo();
    }

    private static NodeMetadata getNodeMetadata() {
        return getNodeInfo().getNodeMetadata();
    }

    private static NodeBrokerInfo getNodeBrokerInfo() {
        return getNodeInfo().getNodeBrokerInfo();
    }

    private static NodeCapabilities getNodeCapabilities() {
        return getNodeInfo().getNodeCapabilities();
    }
}
