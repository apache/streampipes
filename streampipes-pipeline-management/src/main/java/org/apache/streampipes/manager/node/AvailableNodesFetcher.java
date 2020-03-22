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
package org.apache.streampipes.manager.node;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.model.node.NodeInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

public class AvailableNodesFetcher {

    public AvailableNodesFetcher() {

    }

    public List<NodeInfo> fetchNodes() {
        List<String> activeNodes = getActiveNodesFromConsul();
        List<NodeInfo> nodeInfos = new ArrayList<>();
        activeNodes.forEach(activeNode -> {
            try {
                nodeInfos.add(fetchNodeInfo(activeNode));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return nodeInfos;
    }

    private List<String> getActiveNodesFromConsul() {
        return ConsulUtil.getActiveNodeEndpoints();
    }

    private NodeInfo fetchNodeInfo(String activeNode) throws IOException {
        String response = Request
                .Get(activeNode + "/node/info")
                .addHeader("Accept", MediaType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();

        return new Gson().fromJson(response, NodeInfo.class);
    }
}
