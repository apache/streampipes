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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;

import java.util.List;

public class OpcUaAlarmAdapterConfig extends OpcUaConfig {

  private String notifierNodeId;
  private String eventTypeNodeId;
  private List<String> selectedAdditionalFieldNames;
  private String sourceNameFilter;
  private Integer minimumSeverity;

  public String getNotifierNodeId() {
    return notifierNodeId;
  }

  public void setNotifierNodeId(String notifierNodeId) {
    this.notifierNodeId = notifierNodeId;
  }

  public String getEventTypeNodeId() {
    return eventTypeNodeId;
  }

  public void setEventTypeNodeId(String eventTypeNodeId) {
    this.eventTypeNodeId = eventTypeNodeId;
  }

  public List<String> getSelectedAdditionalFieldNames() {
    return selectedAdditionalFieldNames;
  }

  public void setSelectedAdditionalFieldNames(List<String> selectedAdditionalFieldNames) {
    this.selectedAdditionalFieldNames = selectedAdditionalFieldNames;
  }

  public String getSourceNameFilter() {
    return sourceNameFilter;
  }

  public void setSourceNameFilter(String sourceNameFilter) {
    this.sourceNameFilter = sourceNameFilter;
  }

  public Integer getMinimumSeverity() {
    return minimumSeverity;
  }

  public void setMinimumSeverity(Integer minimumSeverity) {
    this.minimumSeverity = minimumSeverity;
  }
}
