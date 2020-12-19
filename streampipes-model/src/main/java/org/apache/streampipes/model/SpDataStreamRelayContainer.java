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
package org.apache.streampipes.model;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;


@RdfsClass(StreamPipes.DATA_STREAM_RELAY_CONTAINER)
@Entity
public class SpDataStreamRelayContainer extends NamedStreamPipesEntity {

    private static final long serialVersionUID = -4675162465357705480L;

    private static final String prefix = "urn:apache.org:relaystreamcontainer:";

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @RdfProperty(StreamPipes.HAS_GROUNDING)
    protected EventGrounding inputGrounding;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @RdfProperty(StreamPipes.HAS_EVENT_RELAY)
    private List<SpDataStreamRelay> outputStreamRelays;

    @RdfProperty(StreamPipes.HAS_EVENT_RELAY_STRATEGY)
    private String eventRelayStrategy;

    @RdfProperty(StreamPipes.DATA_STREAM_RELAY_RUNNING_INSTANCE_ID)
    private String runningStreamRelayInstanceId;

    @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_ID)
    private String deploymentTargetNodeId;

    @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_HOSTNAME)
    private String deploymentTargetNodeHostname;

    @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_PORT)
    private Integer deploymentTargetNodePort;

    public SpDataStreamRelayContainer() {
        super(prefix + RandomStringUtils.randomAlphabetic(6));
        this.outputStreamRelays = new ArrayList<>();
    }

    public SpDataStreamRelayContainer(String elementId, List<SpDataStreamRelay> outputStreamRelays,
                                      String eventRelayStrategy) {
        super(elementId);
        this.outputStreamRelays = outputStreamRelays;
        this.eventRelayStrategy = eventRelayStrategy;
    }

    public SpDataStreamRelayContainer(NamedStreamPipesEntity other) {
        super(other);
    }

    public EventGrounding getInputGrounding() {
        return inputGrounding;
    }

    public void setInputGrounding(EventGrounding inputGrounding) {
        this.inputGrounding = inputGrounding;
    }

    public List<SpDataStreamRelay> getOutputStreamRelays() {
        return outputStreamRelays;
    }

    public void setOutputStreamRelays(List<SpDataStreamRelay> outputStreamRelays) {
        this.outputStreamRelays = outputStreamRelays;
    }

    public String getRunningStreamRelayInstanceId() {
        return runningStreamRelayInstanceId;
    }

    public void setRunningStreamRelayInstanceId(String runningStreamRelayInstanceId) {
        this.runningStreamRelayInstanceId = runningStreamRelayInstanceId;
    }

    public String getEventRelayStrategy() {
        return eventRelayStrategy;
    }

    public void setEventRelayStrategy(String eventRelayStrategy) {
        this.eventRelayStrategy = eventRelayStrategy;
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
}
