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
package org.apache.streampipes.connect.adapter;

import org.apache.http.client.fluent.Request;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NodeControllerService {
    private static final Logger LOG = LoggerFactory.getLogger(NodeControllerService.class);
    
    public static boolean isEdgeOrFogNode(AdapterDescription desc) {
        String baseUrl = generateBaseUrl(desc);
        NodeInfoDescription node = getNodeDescription(baseUrl);
        String nodeType = node.getStaticNodeMetadata().getType();
        LOG.info("Present node type: " + nodeType);
        return nodeType.equals("edge") || nodeType.equals("fog");
    }

    public static TransportProtocol getNodeTransportProtocol(AdapterDescription desc) {
        String baseUrl = generateBaseUrl(desc);
        NodeInfoDescription node = getNodeDescription(baseUrl);
        TransportProtocol transportProtocol = node.getNodeBroker().getNodeTransportProtocol();
        LOG.info("Present node transport protocol: " + transportProtocol.toString());
        return transportProtocol;
    }

    public static NodeInfoDescription getNodeDescription(String baseUrl) {
        String url = baseUrl + "/api/v2/node/info";

        try {
            String payload = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute()
                    .returnContent()
                    .toString();

            return JacksonSerializer
                    .getObjectMapper()
                    .readValue(payload, NodeInfoDescription.class);

        } catch (IOException e) {
            LOG.info("Could not connect to " + url);
            throw new SpRuntimeException(e);
        }
    }

    private static String generateBaseUrl(AdapterDescription desc) {
        return "http://" + desc.getDeploymentTargetNodeHostname() + ":" + desc.getDeploymentTargetNodePort();
    }
}
