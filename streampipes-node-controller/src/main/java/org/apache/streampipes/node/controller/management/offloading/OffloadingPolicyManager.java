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

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.node.monitor.ResourceMetrics;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.offloading.strategies.OffloadingStrategy;
import org.apache.streampipes.node.controller.management.pe.PipelineElementManager;
import org.apache.streampipes.node.controller.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OffloadingPolicyManager {

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String BACKEND_BASE_ROUTE = "streampipes-backend/api/v2/users/admin@streampipes.org";
    private static final String NODE_MANAGEMENT_ONLINE_ENDPOINT = BACKEND_BASE_ROUTE + "/nodes/online";

    private final List<OffloadingStrategy<?>> offloadingStrategies = new ArrayList<>();
    private final List<InvocableStreamPipesEntity> blacklistInvocables = new ArrayList<>();
    private static OffloadingPolicyManager instance;
    private static final Logger LOG = LoggerFactory.getLogger(OffloadingPolicyManager.class.getCanonicalName());
    //private static EvaluationLogger logger = EvaluationLogger.getInstance();

    public static OffloadingPolicyManager getInstance(){
        if(instance == null){
            instance = new OffloadingPolicyManager();
        }
        return instance;
    }

    public void checkPolicies(ResourceMetrics rm){
        List<OffloadingStrategy<?>> violatedPolicies = new ArrayList<>();
        for(OffloadingStrategy strategy : offloadingStrategies){
            strategy.getOffloadingPolicy().addValue(strategy.getResourceProperty().getProperty(rm));
            if(strategy.getOffloadingPolicy().isViolated()){
                String violatedProperty = strategy.getResourceProperty().getClass().getSimpleName();
                LOG.info("Violated policy for resource property {}", violatedProperty);
                violatedPolicies.add(strategy);
            }
        }
        if(!violatedPolicies.isEmpty()){
            //Currently uses the first violated policy. Could be extended to take the degree of policy violation into
            // account
            //TODO: Remove Logger after debugging
            EvaluationLogger.getInstance().logMQTT("Offloading", "offloading triggered", violatedPolicies.get(0).getOffloadingPolicy().getClass().getSimpleName());
            triggerOffloading(violatedPolicies.get(0));
        }
        //Blacklist of entities is cleared when no policies were violated.
        else blacklistInvocables.clear();
    }

    private void triggerOffloading(OffloadingStrategy strategy){
        InvocableStreamPipesEntity offloadEntity = strategy.getSelectionStrategy().select(this.blacklistInvocables);
        EvaluationLogger.getInstance().logMQTT("Offloading", "entity to offload selected");
        if(offloadEntity != null){
            Response resp = PipelineElementManager.getInstance().offload(offloadEntity);

            String appId = offloadEntity.getAppId();
            String runningInstanceId = offloadEntity.getDeploymentRunningInstanceId();
            String pipelineName = offloadEntity.getCorrespondingPipeline();
            int prioScore = offloadEntity.getPriorityScore();

            EvaluationLogger.getInstance().logMQTT(
                    "Offloading",
                    "offloading done",
                    strategy.getOffloadingPolicy().getClass().getSimpleName(),
                    appId + "_prio" + prioScore + "_" + pipelineName);

            if(resp.isSuccess()){
                LOG.info("Successfully offloaded: {} of pipeline: {}", appId, pipelineName);
                strategy.getOffloadingPolicy().reset();
            } else{
                LOG.warn("Failed to offload: {} of pipeline: {}", appId, pipelineName);
                blacklistInvocables.add(offloadEntity);
            }
        } else LOG.info("No pipeline element found to offload");
    }

    public void addOffloadingStrategy(OffloadingStrategy<?> offloadingStrategy){
        this.offloadingStrategies.add(offloadingStrategy);
    }

    public void addOffloadingStrategies(List<OffloadingStrategy<?>> offloadingStrategies){
        offloadingStrategies.forEach(this::addOffloadingStrategy);
    }

    public List<NodeInfoDescription> getOnlineNodes(){
        String endpoint = generateNodeManagementOnlineNodesEndpoint();
        return HttpUtils.get(endpoint, new TypeReference<List<NodeInfoDescription>>(){});
    }

    private String generateNodeManagementOnlineNodesEndpoint() {
        return HTTP_PROTOCOL
                + NodeConfiguration.getBackendHost()
                + COLON
                + NodeConfiguration.getBackendPort()
                + SLASH
                + NODE_MANAGEMENT_ONLINE_ENDPOINT;
    }

}
