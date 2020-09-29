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

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.List;

@TsModel
public class NodeMetadata {

    private String nodeAddress;
    private String nodeModel;
    private List<String> nodeLocationTags;

    public NodeMetadata() {
    }

    public NodeMetadata(String nodeAddress, String nodeModel, List<String> nodeLocationTags) {
        this.nodeAddress = nodeAddress;
        this.nodeModel = nodeModel;
        this.nodeLocationTags = nodeLocationTags;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getNodeModel() {
        return nodeModel;
    }

    public void setNodeModel(String nodeModel) {
        this.nodeModel = nodeModel;
    }

    public List<String> getNodeLocationTags() {
        return nodeLocationTags;
    }

    public void setNodeLocationTags(List<String> nodeLocationTags) {
        this.nodeLocationTags = nodeLocationTags;
    }
}
