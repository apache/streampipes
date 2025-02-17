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
import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TsModel
public class PipelineElementTemplate implements Storable {

  List<Map<String, Object>> templateConfigs;

  @JsonAlias("_id")
  private @SerializedName("_id") String elementId;

  @JsonAlias("_rev")
  private @SerializedName("_rev") String rev;
  private String templateName;
  private String templateDescription;
  private String basePipelineElementAppId;

  public PipelineElementTemplate(String templateName,
                                 String templateDescription,
                                 List<Map<String, Object>> templateConfigs) {
    this.templateName = templateName;
    this.templateDescription = templateDescription;
    this.templateConfigs = templateConfigs;
  }

  public PipelineElementTemplate() {
    this.elementId = UUID.randomUUID().toString();
    this.templateConfigs = new ArrayList<>();
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

  public List<Map<String, Object>> getTemplateConfigs() {
    return templateConfigs;
  }

  public void setTemplateConfigs(List<Map<String, Object>> templateConfigs) {
    this.templateConfigs = templateConfigs;
  }

  public String getBasePipelineElementAppId() {
    return basePipelineElementAppId;
  }

  public void setBasePipelineElementAppId(String basePipelineElementAppId) {
    this.basePipelineElementAppId = basePipelineElementAppId;
  }

  public String getCouchDbId() {
    return elementId;
  }

  public String getCouchDbRev() {
    return rev;
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
}

