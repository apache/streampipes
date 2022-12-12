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

import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.UUID;

@TsModel
public class PipelineElementTemplate {

  Map<String, PipelineElementTemplateConfig> templateConfigs;
  @JsonProperty("_id")
  private @SerializedName("_id") String couchDbId;
  @JsonProperty("_rev")
  private @SerializedName("_rev") String couchDbRev;
  private String templateName;
  private String templateDescription;
  private String basePipelineElementAppId;

  public PipelineElementTemplate(String templateName,
                                 String templateDescription,
                                 Map<String, PipelineElementTemplateConfig> templateConfigs) {
    this.templateName = templateName;
    this.templateDescription = templateDescription;
    this.templateConfigs = templateConfigs;
  }

  public PipelineElementTemplate() {
    this.couchDbId = UUID.randomUUID().toString();
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateDescription() {
    return templateDescription;
  }

  public void setTemplateDescription(String templateDescription) {
    this.templateDescription = templateDescription;
  }

  public Map<String, PipelineElementTemplateConfig> getTemplateConfigs() {
    return templateConfigs;
  }

  public void setTemplateConfigs(Map<String, PipelineElementTemplateConfig> templateConfigs) {
    this.templateConfigs = templateConfigs;
  }

  public String getBasePipelineElementAppId() {
    return basePipelineElementAppId;
  }

  public void setBasePipelineElementAppId(String basePipelineElementAppId) {
    this.basePipelineElementAppId = basePipelineElementAppId;
  }

  public String getCouchDbId() {
    return couchDbId;
  }

  public void setCouchDbId(String couchDbId) {
    this.couchDbId = couchDbId;
  }

  public String getCouchDbRev() {
    return couchDbRev;
  }

  public void setCouchDbRev(String couchDbRev) {
    this.couchDbRev = couchDbRev;
  }
}

