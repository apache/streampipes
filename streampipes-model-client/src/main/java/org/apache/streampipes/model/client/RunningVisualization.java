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

package org.apache.streampipes.model.client;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class RunningVisualization {

  private @SerializedName("_id") String id;
  private @SerializedName("_rev") String rev;

  private String pipelineId;
  private String pipelineName;
  private String consumerUrl;
  private String description;
  private String title;

  public RunningVisualization(String pipelineId,
                              String pipelineName,
                              String consumerUrl,
                              String description,
                              String title) {

    this.id = UUID.randomUUID().toString();
    this.pipelineId = pipelineId;
    this.pipelineName = pipelineName;
    this.consumerUrl = consumerUrl;
    this.description = description;
    this.title = title;
  }

  public String getConsumerUrl() {
    return consumerUrl;
  }

  public void setConsumerUrl(String consumerUrl) {
    this.consumerUrl = consumerUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPipelineName() {
    return pipelineName;
  }

  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
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

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }


}
