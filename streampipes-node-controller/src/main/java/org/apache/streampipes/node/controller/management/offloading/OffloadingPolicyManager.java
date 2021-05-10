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

package org.apache.streampipes.node.controller.management.offloading;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.node.monitor.ResourceMetrics;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.offloading.model.OffloadingStrategy;
import org.apache.streampipes.node.controller.management.pe.InvocableElementManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OffloadingPolicyManager {

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";

    private final List<OffloadingStrategy<?>> offloadingStrategies = new ArrayList<>();
    private static OffloadingPolicyManager instance;
    private static final Logger LOG = LoggerFactory.getLogger(OffloadingPolicyManager.class.getCanonicalName());

    public static OffloadingPolicyManager getInstance(){
        if(instance == null){
            instance = new OffloadingPolicyManager();
        }
        return instance;
    }

    public void checkPolicies(ResourceMetrics rm){
        for(OffloadingStrategy strategy:offloadingStrategies){
            strategy.getOffloadingPolicy().addValue(strategy.getResourceProperty().getProperty(rm));
            if(strategy.getOffloadingPolicy().isViolated()){
                InvocableStreamPipesEntity offloadEntity = strategy.getSelectionStrategy().selectEntity();
                if(offloadEntity != null){
                    Response resp = InvocableElementManager.getInstance().postOffloadRequest(offloadEntity);
                    if(resp.isSuccess())
                        LOG.info("Successfully offloaded: " + offloadEntity.getAppId()
                                + " from Pipeline: " + offloadEntity.getCorrespondingPipeline());
                    else LOG.info("Failed to offload: " + offloadEntity.getAppId()
                            + " from Pipeline: " + offloadEntity.getCorrespondingPipeline());
                }else LOG.info("No entity to offload found");
            }
        }
    }

    public void addOffloadingStrategy(OffloadingStrategy<?> offloadingStrategy){
        this.offloadingStrategies.add(offloadingStrategy);
    }

    public List<NodeInfoDescription> getOnlineNodes(){
        try {
            String url = generateNodeManagementOnlineNodesEndpoint();
            org.apache.http.client.fluent.Response resp = Request.Get(url).execute();
            ArrayList<NodeInfoDescription> onlineNodes =
                    JacksonSerializer.getObjectMapper().readValue(resp.returnContent().asString(),
                    ArrayList.class);
            return onlineNodes;
        } catch (IOException e) {
            throw new SpRuntimeException(e);
        }
    }

    private String generateNodeManagementOnlineNodesEndpoint() {
        return HTTP_PROTOCOL
                + NodeConfiguration.getBackendHost()
                + COLON
                + NodeConfiguration.getBackendPort()
                + SLASH
                + "streampipes-backend/api/v2/users/admin@streampipes.org/nodes/online";
    }

}
