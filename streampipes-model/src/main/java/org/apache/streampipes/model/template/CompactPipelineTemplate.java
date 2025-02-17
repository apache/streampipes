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

package org.apache.streampipes.model.template;

import org.apache.streampipes.model.pipeline.compact.CompactPipeline;
import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@TsModel
public class CompactPipelineTemplate implements Storable {

  @JsonAlias("id")
  protected @SerializedName("_id") String elementId;

  @JsonAlias("_rev")
  protected @SerializedName("_rev") String rev;

  private String name;
  private String description;
  private List<CompactPipelineElement> pipeline;
  private PipelinePlaceholders placeholders;

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

  public List<CompactPipelineElement> getPipeline() {
    return pipeline;
  }

  public void setPipeline(List<CompactPipelineElement> pipeline) {
    this.pipeline = pipeline;
  }

  public PipelinePlaceholders getPlaceholders() {
    return placeholders;
  }

  public void setPlaceholders(PipelinePlaceholders placeholders) {
    this.placeholders = placeholders;
  }

  @Override
  public String getRev() {
    return rev;
  }

  @Override
  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return elementId;
  }

  @Override
  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public CompactPipeline toCompactPipeline() {
    return new CompactPipeline(
        null,
        null,
        null,
        this.getPipeline(),
        null
    );
  }
}
