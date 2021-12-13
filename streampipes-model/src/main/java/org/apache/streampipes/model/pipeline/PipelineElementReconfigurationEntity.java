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
package org.apache.streampipes.model.pipeline;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

public class PipelineElementReconfigurationEntity {

    private String deploymentRunningInstanceId;
    private String pipelineElementName;
    private String deploymentTargetNodeId;
    private String deploymentTargetNodeHostname;
    private Integer deploymentTargetNodePort;
    private List<StaticProperty> reconfiguredStaticProperties;

    public PipelineElementReconfigurationEntity() {
        this.reconfiguredStaticProperties = new ArrayList<>();
    }

    public PipelineElementReconfigurationEntity(String deploymentRunningInstanceId,
                                                String pipelineElementName, String deploymentTargetNodeId,
                                                String deploymentTargetNodeHostname, Integer deploymentTargetNodePort,
                                                List<StaticProperty> reconfiguredStaticProperties) {
        this.deploymentRunningInstanceId = deploymentRunningInstanceId;
        this.pipelineElementName = pipelineElementName;
        this.deploymentTargetNodeId = deploymentTargetNodeId;
        this.deploymentTargetNodeHostname = deploymentTargetNodeHostname;
        this.deploymentTargetNodePort = deploymentTargetNodePort;
        this.reconfiguredStaticProperties = reconfiguredStaticProperties;
    }

    public PipelineElementReconfigurationEntity(InvocableStreamPipesEntity entity){
        this.deploymentRunningInstanceId = entity.getDeploymentRunningInstanceId();
        this.pipelineElementName = entity.getName();
        this.deploymentTargetNodeId = entity.getDeploymentTargetNodeId();
        this.deploymentTargetNodeHostname = entity.getDeploymentTargetNodeHostname();
        this.deploymentTargetNodePort = entity.getDeploymentTargetNodePort();
        this.reconfiguredStaticProperties = new ArrayList<>();
    }

    public String getDeploymentRunningInstanceId() {
        return deploymentRunningInstanceId;
    }

    public void setDeploymentRunningInstanceId(String deploymentRunningInstanceId) {
        this.deploymentRunningInstanceId = deploymentRunningInstanceId;
    }

    public String getPipelineElementName() {
        return pipelineElementName;
    }

    public void setPipelineElementName(String pipelineElementName) {
        this.pipelineElementName = pipelineElementName;
    }

    public String getDeploymentTargetNodeId() {
        return deploymentTargetNodeId;
    }

    public void setDeploymentTargetNodeId(String deploymentTargetNodeId) {
        this.deploymentTargetNodeId = deploymentTargetNodeId;
    }

    public String getDeploymentTargetNodeHostname() {
        return deploymentTargetNodeHostname;
    }

    public void setDeploymentTargetNodeHostname(String deploymentTargetNodeHostname) {
        this.deploymentTargetNodeHostname = deploymentTargetNodeHostname;
    }

    public Integer getDeploymentTargetNodePort() {
        return deploymentTargetNodePort;
    }

    public void setDeploymentTargetNodePort(Integer deploymentTargetNodePort) {
        this.deploymentTargetNodePort = deploymentTargetNodePort;
    }

    public List<StaticProperty> getReconfiguredStaticProperties() {
        return reconfiguredStaticProperties;
    }

    public void setReconfiguredStaticProperties(List<StaticProperty> reconfiguredStaticProperties) {
        this.reconfiguredStaticProperties = reconfiguredStaticProperties;
    }

    public void addReconfiguredStaticProperties(StaticProperty reconfiguredStaticProperty) {
        this.reconfiguredStaticProperties.add(reconfiguredStaticProperty);
    }

    public void removeReconfiguredStaticProperties(StaticProperty reconfiguredStaticProperty){
        this.reconfiguredStaticProperties.remove(reconfiguredStaticProperty);
    }
}
