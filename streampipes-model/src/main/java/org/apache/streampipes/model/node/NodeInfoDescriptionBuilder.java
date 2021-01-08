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

import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.node.container.DeploymentContainer;
import org.apache.streampipes.model.node.meta.GeoLocation;
import org.apache.streampipes.model.node.meta.StaticNodeMetadata;
import org.apache.streampipes.model.node.resources.NodeResource;

import java.util.ArrayList;
import java.util.List;

public class NodeInfoDescriptionBuilder {

    private final NodeInfoDescription nodeInfoDescription;
    private final NodeBrokerDescription nodeBroker;
    private final StaticNodeMetadata staticNodeMetadata;
    private NodeResource nodeResources;
    private List<DeploymentContainer> registeredContainers;
    private final List<String> supportedElements;

    public NodeInfoDescriptionBuilder(String id) {
        this.nodeInfoDescription = new NodeInfoDescription(id);
        this.nodeBroker = new NodeBrokerDescription();
        this.staticNodeMetadata = new StaticNodeMetadata();
        this.nodeResources = new NodeResource();
        this.registeredContainers = new ArrayList<>();
        this.supportedElements = new ArrayList<>();
    }

    public static NodeInfoDescriptionBuilder create(String id) {
        return new NodeInfoDescriptionBuilder(id);
    }

    public NodeInfoDescriptionBuilder withHostname(String hostname) {
        this.nodeInfoDescription.setHostname(hostname);
        return this;
    }

    public NodeInfoDescriptionBuilder withPort(int port) {
        this.nodeInfoDescription.setPort(port);
        return this;
    }

    public NodeInfoDescriptionBuilder withNodeType(String type) {
        this.staticNodeMetadata.setType(type);
        return this;
    }

    public NodeInfoDescriptionBuilder withLocationTags(List<String> locationTags) {
        this.staticNodeMetadata.setLocationTags(locationTags);
        return this;
    }

    public NodeInfoDescriptionBuilder withNodeModel(String model) {
        this.staticNodeMetadata.setModel(model);
        return this;
    }

    public NodeInfoDescriptionBuilder withGeolocation(GeoLocation geoLocation) {
        this.staticNodeMetadata.setGeoLocation(geoLocation);
        return this;
    }

    public NodeInfoDescriptionBuilder withNodeBroker(String host, int port) {
        MqttTransportProtocol tp = new MqttTransportProtocol();
        tp.setBrokerHostname(host);
        tp.setPort(port);
        this.nodeBroker.setNodeTransportProtocol(tp);
        return this;
    }

    public NodeInfoDescriptionBuilder withNodeBroker(TransportProtocol tp) {
        this.nodeBroker.setNodeTransportProtocol(tp);
        return this;
    }

    public NodeInfoDescriptionBuilder withSupportedElements(List<String> supportedElements) {
        this.nodeInfoDescription.setSupportedElements(supportedElements);
        return this;
    }

    public NodeInfoDescriptionBuilder withRegisteredContainers(List<DeploymentContainer> registeredContainers) {
        this.registeredContainers = registeredContainers;
        return this;
    }

    public NodeInfoDescriptionBuilder withNodeResources(NodeResource nodeResources) {
        this.nodeResources = nodeResources;
        return this;
    }

    public NodeInfoDescriptionBuilder staticNodeMetadata(String type, String model, GeoLocation geoLocation,
                                                         List<String> locationTags) {
        this.staticNodeMetadata.setType(type);
        this.staticNodeMetadata.setModel(model);
        this.staticNodeMetadata.setGeoLocation(geoLocation);
        this.staticNodeMetadata.setLocationTags(locationTags);
        return this;
    }

    public NodeInfoDescription build() {
        nodeInfoDescription.setStaticNodeMetadata(staticNodeMetadata);
        nodeInfoDescription.setNodeBroker(nodeBroker);
        nodeInfoDescription.setRegisteredContainers(registeredContainers);
        nodeInfoDescription.setNodeResources(nodeResources);
        nodeInfoDescription.setSupportedElements(supportedElements);
        nodeInfoDescription.setActive(true);
        return nodeInfoDescription;
    }
}
