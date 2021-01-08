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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public enum NodeClusterManager {
    INSTANCE;


    private static final Logger LOG =
            LoggerFactory.getLogger(NodeClusterManager.class.getCanonicalName());


    public boolean updateNodeInfoDescription(NodeInfoDescription desc) {
        boolean successfullyUpdated = false;
        try {
            String body = JacksonSerializer.getObjectMapper().writeValueAsString(desc);
            String url = makeNodeControllerEndpoint(desc);

            LOG.info("Trying to update description for node controller: " + url);

            boolean connected = false;
            while (!connected) {
                connected = put(url, body);

                if (!connected) {
                    LOG.info("Retrying in 5 seconds");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            successfullyUpdated = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return successfullyUpdated;
    }

    private String makeNodeControllerEndpoint(NodeInfoDescription desc) {
        return "http://" + desc.getHostname() + ":" + desc.getPort() + "/api/v2/node/info";
    }

    private boolean put(String url, String body) {
        try {
            Request.Put(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
