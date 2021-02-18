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
package org.apache.streampipes.model.monitoring;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.List;

@TsModel
public class PipelineMonitoringInfo {

  private String pipelineId;
  private long createdAt;
  private long startedAt;

  private List<PipelineElementMonitoringInfo> pipelineElementMonitoringInfo;

  public PipelineMonitoringInfo() {
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public List<PipelineElementMonitoringInfo> getPipelineElementMonitoringInfo() {
    return pipelineElementMonitoringInfo;
  }

  public void setPipelineElementMonitoringInfo(List<PipelineElementMonitoringInfo> pipelineElementMonitoringInfo) {
    this.pipelineElementMonitoringInfo = pipelineElementMonitoringInfo;
  }

  public long getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(long startedAt) {
    this.startedAt = startedAt;
  }
}
