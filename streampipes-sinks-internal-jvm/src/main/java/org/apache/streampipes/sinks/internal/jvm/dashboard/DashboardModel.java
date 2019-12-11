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

package org.apache.streampipes.sinks.internal.jvm.dashboard;

import org.apache.streampipes.model.schema.EventSchema;

public class DashboardModel {

  private String pipelineId;
  private EventSchema schema;
  private String visualizationName;
  private String topic;

  public static DashboardModel from(DashboardParameters params) {
    DashboardModel model = new DashboardModel();
    model.setPipelineId(params.getPipelineId());
    model.setSchema(params.getSchema());
    model.setVisualizationName(params.getVisualizationName());
    model.setTopic(params.getElementId());

    return model;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public EventSchema getSchema() {
    return schema;
  }

  public void setSchema(EventSchema schema) {
    this.schema = schema;
  }

  public String getVisualizationName() {
    return visualizationName;
  }

  public void setVisualizationName(String visualizationName) {
    this.visualizationName = visualizationName;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }
}
