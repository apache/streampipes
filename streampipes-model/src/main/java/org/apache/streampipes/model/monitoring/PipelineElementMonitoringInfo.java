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

import java.util.ArrayList;
import java.util.List;

public class PipelineElementMonitoringInfo {

  private String pipelineElementId;
  private String pipelineElementName;

  private boolean inputTopicInfoExists;
  private boolean outputTopicInfoExists;

  private List<PipelineElementTopicInfo> inputTopicInfo;
  private PipelineElementTopicInfo outputTopicInfo;

  public PipelineElementMonitoringInfo() {
    this.inputTopicInfo = new ArrayList<>();
  }

  public String getPipelineElementId() {
    return pipelineElementId;
  }

  public void setPipelineElementId(String pipelineElementId) {
    this.pipelineElementId = pipelineElementId;
  }

  public String getPipelineElementName() {
    return pipelineElementName;
  }

  public void setPipelineElementName(String pipelineElementName) {
    this.pipelineElementName = pipelineElementName;
  }

  public List<PipelineElementTopicInfo> getInputTopicInfo() {
    return inputTopicInfo;
  }

  public void setInputTopicInfo(List<PipelineElementTopicInfo> inputTopicInfo) {
    this.inputTopicInfo = inputTopicInfo;
  }

  public PipelineElementTopicInfo getOutputTopicInfo() {
    return outputTopicInfo;
  }

  public void setOutputTopicInfo(PipelineElementTopicInfo outputTopicInfo) {
    this.outputTopicInfo = outputTopicInfo;
  }

  public boolean isInputTopicInfoExists() {
    return inputTopicInfoExists;
  }

  public void setInputTopicInfoExists(boolean inputTopicInfoExists) {
    this.inputTopicInfoExists = inputTopicInfoExists;
  }

  public boolean isOutputTopicInfoExists() {
    return outputTopicInfoExists;
  }

  public void setOutputTopicInfoExists(boolean outputTopicInfoExists) {
    this.outputTopicInfoExists = outputTopicInfoExists;
  }
}
