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
package org.apache.streampipes.model.canvas;

import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

@TsModel
public class PipelineCanvasMetadata {

  @JsonProperty("_id")
  private @SerializedName("_id") String id;

  @JsonProperty("_rev")
  private @SerializedName("_rev") String rev;

  private String pipelineId;
  private Map<String, PipelineElementMetadata> pipelineElementMetadata;
  private List<PipelineCanvasComment> comments;

  public PipelineCanvasMetadata() {
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public Map<String, PipelineElementMetadata> getPipelineElementMetadata() {
    return pipelineElementMetadata;
  }

  public void setPipelineElementMetadata(Map<String, PipelineElementMetadata> pipelineElementMetadata) {
    this.pipelineElementMetadata = pipelineElementMetadata;
  }

  public List<PipelineCanvasComment> getComments() {
    return comments;
  }

  public void setComments(List<PipelineCanvasComment> comments) {
    this.comments = comments;
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
}
