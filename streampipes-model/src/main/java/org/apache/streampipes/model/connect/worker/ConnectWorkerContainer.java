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

package org.apache.streampipes.model.connect.worker;

import com.google.gson.annotations.SerializedName;
import io.fogsy.empire.annotations.Namespaces;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass(StreamPipes.CONNECT_WORKER_CONTAINER)
@Entity
public class ConnectWorkerContainer extends UnnamedStreamPipesEntity {

    @RdfProperty("sp:couchDBId")
    private @SerializedName("_id") String id;

    private @SerializedName("_rev") String rev;

    @RdfProperty("sp:endpointUrl")
    private String endpointUrl;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:protocols")
    private List<ProtocolDescription> protocols;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<AdapterDescription> adapters;

    @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_ID)
    private String deploymentTargetNodeId;

    @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_HOSTNAME)
    private String deploymentTargetNodeHostname;

    @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_PORT)
    private Integer deploymentTargetNodePort;

    public ConnectWorkerContainer() {
        super();
        this.adapters = new ArrayList<>();
        this.protocols = new ArrayList<>();
    }

    public ConnectWorkerContainer(String endpointUrl, List<ProtocolDescription> protocols,
                                  List<AdapterDescription> adapters) {
        this.endpointUrl = endpointUrl;
        this.protocols = protocols;
        this.adapters = adapters;
    }

    public ConnectWorkerContainer(String endpointUrl, List<ProtocolDescription> protocols,
                                  List<AdapterDescription> adapters, String deploymentTargetNodeId,
                                  String deploymentTargetNodeHostname, int deploymentTargetNodePort) {
        this.endpointUrl = endpointUrl;
        this.protocols = protocols;
        this.adapters = adapters;
        this.deploymentTargetNodeId = deploymentTargetNodeId;
        this.deploymentTargetNodeHostname = deploymentTargetNodeHostname;
        this.deploymentTargetNodePort = deploymentTargetNodePort;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public List<ProtocolDescription> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<ProtocolDescription> protocols) {
        this.protocols = protocols;
    }

    public List<AdapterDescription> getAdapters() {
        return adapters;
    }

    public void setAdapters(List<AdapterDescription> adapters) {
        this.adapters = adapters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
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
