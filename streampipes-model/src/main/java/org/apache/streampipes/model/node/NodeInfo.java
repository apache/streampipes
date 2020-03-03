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

import java.util.List;

public class NodeInfo {

    private String nodeControllerId;
    private int nodeControllerPort;
    private NodeMetadata nodeMetadata;
    private NodeBrokerInfo nodeBrokerInfo;
    private NodeCapabilities nodeCapabilities;
    private List<String> supportedPipelineElementAppIds;

    public NodeInfo() {

    }

    public String getNodeControllerId() {
        return nodeControllerId;
    }

    public void setNodeControllerId(String nodeControllerId) {
        this.nodeControllerId = nodeControllerId;
    }

    public int getNodeControllerPort() {
        return nodeControllerPort;
    }

    public void setNodeControllerPort(int nodeControllerPort) {
        this.nodeControllerPort = nodeControllerPort;
    }

    public NodeMetadata getNodeMetadata() {
        return nodeMetadata;
    }

    public void setNodeMetadata(NodeMetadata nodeMetadata) {
        this.nodeMetadata = nodeMetadata;
    }

    public NodeCapabilities getNodeCapabilities() {
        return nodeCapabilities;
    }

    public void setNodeCapabilities(NodeCapabilities nodeCapabilities) {
        this.nodeCapabilities = nodeCapabilities;
    }

    public List<String> getSupportedPipelineElementAppIds() {
        return supportedPipelineElementAppIds;
    }

    public void setSupportedPipelineElementAppIds(List<String> supportedPipelineElementAppIds) {
        this.supportedPipelineElementAppIds = supportedPipelineElementAppIds;
    }

    public NodeBrokerInfo getNodeBrokerInfo() {
        return nodeBrokerInfo;
    }

    public void setNodeBrokerInfo(NodeBrokerInfo nodeBrokerInfo) {
        this.nodeBrokerInfo = nodeBrokerInfo;
    }
}
