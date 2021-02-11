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

  private boolean consumedMessageInfoExists;
  private boolean producedMessageInfoExists;

  private List<ConsumedMessagesInfo> consumedMessagesInfos;
  private ProducedMessagesInfo producedMessagesInfo;

  public PipelineElementMonitoringInfo() {
    this.consumedMessagesInfos = new ArrayList<>();
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

  public List<ConsumedMessagesInfo> getConsumedMessagesInfos() {
    return consumedMessagesInfos;
  }

  public void setConsumedMessagesInfos(List<ConsumedMessagesInfo> consumedMessagesInfos) {
    this.consumedMessagesInfos = consumedMessagesInfos;
  }

  public ProducedMessagesInfo getProducedMessagesInfo() {
    return producedMessagesInfo;
  }

  public void setProducedMessagesInfo(ProducedMessagesInfo producedMessagesInfo) {
    this.producedMessagesInfo = producedMessagesInfo;
  }

  public boolean isConsumedMessageInfoExists() {
    return consumedMessageInfoExists;
  }

  public void setConsumedMessageInfoExists(boolean consumedMessageInfoExists) {
    this.consumedMessageInfoExists = consumedMessageInfoExists;
  }

  public boolean isProducedMessageInfoExists() {
    return producedMessageInfoExists;
  }

  public void setProducedMessageInfoExists(boolean producedMessageInfoExists) {
    this.producedMessageInfoExists = producedMessageInfoExists;
  }

}
