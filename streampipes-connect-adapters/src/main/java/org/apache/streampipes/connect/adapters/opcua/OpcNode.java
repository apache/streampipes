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

package org.apache.streampipes.connect.adapters.opcua;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.apache.streampipes.sdk.utils.Datatypes;

public class OpcNode {
    String label;
    Datatypes type;
    NodeId nodeId;
    int opcUnitId;

    public OpcNode(String label, Datatypes type, NodeId nodeId) {
        this.label = label;
        this.type = type;
        this.nodeId = nodeId;
        this.opcUnitId = 0;
    }

    public OpcNode(String label, Datatypes type, NodeId nodeId, Integer opcUnitId){
        this.label = label;
        this.type = type;
        this.nodeId = nodeId;
        this.opcUnitId = opcUnitId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Datatypes getType() {
        return type;
    }

    public void setType(Datatypes type) {
        this.type = type;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public void setNodeId(NodeId nodeId) {
        this.nodeId = nodeId;
    }
}
