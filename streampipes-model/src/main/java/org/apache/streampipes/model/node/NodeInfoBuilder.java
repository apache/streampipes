/*
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
package org.apache.streampipes.model.node;

import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.node.resources.hardware.HardwareResource;
import org.apache.streampipes.model.node.resources.interfaces.AccessibleSensorActuatorResource;
import org.apache.streampipes.model.node.resources.software.SoftwareResource;

import java.util.ArrayList;
import java.util.List;

public class NodeInfoBuilder {
    private NodeInfo nodeInfo;
    private NodeMetadata nodeMetadata;
    private NodeResources nodeResources;
    private List<String> supportedPipelineElementAppIds;
    private NodeBrokerInfo nodeBrokerInfo;

    private NodeInfoBuilder(String nodeId) {
        this.nodeInfo = new NodeInfo();
        this.nodeInfo.setNodeControllerId(nodeId);
        this.nodeMetadata = new NodeMetadata();
        this.nodeResources = new NodeResources();
        this.supportedPipelineElementAppIds = new ArrayList<>();
        this.nodeBrokerInfo = new NodeBrokerInfo();
    }

    public static NodeInfoBuilder create(String nodeId) {
       return new NodeInfoBuilder(nodeId);
    }

//    public NodeInfoBuilder withNodeName(String nodeName) {
//        this.nodeMetadata.setNodeName(nodeName);
//        return this;
//    }

    public NodeInfoBuilder withNodeControllerPort(int nodeControllerPort) {
        this.nodeInfo.setNodeControllerPort(nodeControllerPort);
        return this;
    }

    public NodeInfoBuilder withNodeHost(String nodeHost) {
        this.nodeMetadata.setNodeAddress(nodeHost);
        return this;
    }

    public NodeInfoBuilder withNodeType(String nodeType) {
        this.nodeMetadata.setNodeType(nodeType);
        return this;
    }

    public NodeInfoBuilder withNodeLocation(List<String> nodeLocationTags) {
        this.nodeMetadata.setNodeLocationTags(nodeLocationTags);
        return this;
    }

    public NodeInfoBuilder withNodeModel(String nodeModel) {
        this.nodeMetadata.setNodeModel(nodeModel);
        return this;
    }

    public NodeInfoBuilder withSupportedPipelineElements(List<String> supportedPipelineElementAppIds) {
        this.supportedPipelineElementAppIds = supportedPipelineElementAppIds;
        return this;
    }

    public NodeInfoBuilder withJmsTransportProtocol(String brokerHost, Integer brokerPort) {
        //this.nodeBrokerInfo.setTransportProtocol(makeJmsTransportProtocol(brokerHost, brokerPort));
        this.nodeBrokerInfo.setHost(brokerHost);
        this.nodeBrokerInfo.setPort(brokerPort);
        return this;
    }

    public NodeInfoBuilder withNodeResources(NodeResources nodeResources) {
        this.nodeResources = nodeResources;
        return this;
    }

    public NodeInfoBuilder withNodeResources(HardwareResource hardwareResource, SoftwareResource softwareResource, List<AccessibleSensorActuatorResource> acessibleSAResourceList) {
        this.nodeResources.setHardwareResource(hardwareResource);
        this.nodeResources.setSoftwareResource(softwareResource);
        this.nodeResources.setAccessibleSensorActuatorResource(acessibleSAResourceList);
        return this;
    }

    private JmsTransportProtocol makeJmsTransportProtocol(String brokerHost, Integer brokerPort) {
        return new JmsTransportProtocol(brokerHost, brokerPort);
    }


    public NodeInfo build() {
        nodeInfo.setNodeMetadata(nodeMetadata);
        nodeInfo.setNodeResources(nodeResources);
        nodeInfo.setNodeBrokerInfo(nodeBrokerInfo);
        nodeInfo.setSupportedPipelineElementAppIds(supportedPipelineElementAppIds);
        return nodeInfo;
    }
}
