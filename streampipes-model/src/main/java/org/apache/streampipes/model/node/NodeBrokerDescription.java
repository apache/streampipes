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

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.NODE_BROKER_DESCRIPTION)
@Entity
@TsModel
public class NodeBrokerDescription extends UnnamedStreamPipesEntity {

    @RdfProperty(StreamPipes.HAS_TRANSPORT_PROTOCOL)
    private TransportProtocol nodeTransportProtocol;

    public NodeBrokerDescription() {
        super();
    }

    public NodeBrokerDescription(String elementId) {
        super(elementId);
    }

    public NodeBrokerDescription(TransportProtocol nodeTransportProtocol) {
        this.nodeTransportProtocol = nodeTransportProtocol;
    }

    public NodeBrokerDescription(NodeBrokerDescription other, TransportProtocol nodeTransportProtocol) {
        super(other);
        this.nodeTransportProtocol = nodeTransportProtocol;
    }

    public TransportProtocol getNodeTransportProtocol() {
        return nodeTransportProtocol;
    }

    public void setNodeTransportProtocol(TransportProtocol nodeTransportProtocol) {
        this.nodeTransportProtocol = nodeTransportProtocol;
    }

}
