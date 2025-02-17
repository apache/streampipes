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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class Pipeline implements Storable {

  private List<DataSinkInvocation> actions;

  private boolean running;
  private boolean restartOnSystemReboot;
  private boolean valid = true;

  private long startedAt;
  private long createdAt;

  private boolean publicElement;

  private String createdByUser;
  private List<String> pipelineNotifications;

  private PipelineHealthStatus healthStatus;

  @JsonProperty("_id")
  private @SerializedName("_id")
  String pipelineId;

  @JsonProperty("_rev")
  private @SerializedName("_rev")
  String rev;

  protected List<DataProcessorInvocation> sepas;

  protected List<SpDataStream> streams;

  protected String name;
  protected String description;

  public Pipeline() {
    super();
    this.actions = new ArrayList<>();
    this.pipelineNotifications = new ArrayList<>();
    this.sepas = new ArrayList<>();
    this.streams = new ArrayList<>();
  }

  public List<DataSinkInvocation> getActions() {
    return actions;
  }

  public void setActions(List<DataSinkInvocation> actions) {
    this.actions = actions;
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public long getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(long startedAt) {
    this.startedAt = startedAt;
  }

  public boolean isPublicElement() {
    return publicElement;
  }

  public void setPublicElement(boolean publicElement) {
    this.publicElement = publicElement;
  }

  public String getCreatedByUser() {
    return createdByUser;
  }

  public void setCreatedByUser(String createdByUser) {
    this.createdByUser = createdByUser;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return pipelineId;
  }

  @Override
  public void setElementId(String elementId) {
    this.pipelineId = elementId;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isRestartOnSystemReboot() {
    return restartOnSystemReboot;
  }

  public void setRestartOnSystemReboot(boolean restartOnSystemReboot) {
    this.restartOnSystemReboot = restartOnSystemReboot;
  }

  public List<String> getPipelineNotifications() {
    return pipelineNotifications;
  }

  public void setPipelineNotifications(List<String> pipelineNotifications) {
    this.pipelineNotifications = pipelineNotifications;
  }

  public PipelineHealthStatus getHealthStatus() {
    return healthStatus;
  }

  public void setHealthStatus(PipelineHealthStatus healthStatus) {
    this.healthStatus = healthStatus;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public List<DataProcessorInvocation> getSepas() {
    return sepas;
  }

  public void setSepas(List<DataProcessorInvocation> sepas) {
    this.sepas = sepas;
  }

  public List<SpDataStream> getStreams() {
    return streams;
  }

  public void setStreams(List<SpDataStream> streams) {
    this.streams = streams;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Pipeline clone() {
    Pipeline pipeline = new Pipeline();
    pipeline.setName(name);
    pipeline.setDescription(description);
    pipeline.setSepas(sepas);
    pipeline.setStreams(streams);
    pipeline.setActions(actions);
    pipeline.setCreatedByUser(createdByUser);
    pipeline.setCreatedAt(createdAt);
    pipeline.setPipelineId(pipelineId);
    pipeline.setHealthStatus(healthStatus);
    pipeline.setPipelineNotifications(pipelineNotifications);
    pipeline.setRev(rev);
    pipeline.setValid(valid);

    return pipeline;
  }
}
