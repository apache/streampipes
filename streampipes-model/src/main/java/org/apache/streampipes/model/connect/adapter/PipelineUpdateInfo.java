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
package org.apache.streampipes.model.connect.adapter;

import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TsModel
public class PipelineUpdateInfo {

  private String pipelineId;
  private String pipelineName;
  private boolean canAutoMigrate;
  private String migrationInfo;
  private Map<String, List<PipelineElementValidationInfo>> validationInfos;

  public PipelineUpdateInfo() {
    this.validationInfos = new HashMap<>();
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public String getPipelineName() {
    return pipelineName;
  }

  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
  }

  public boolean isCanAutoMigrate() {
    return canAutoMigrate;
  }

  public void setCanAutoMigrate(boolean canAutoMigrate) {
    this.canAutoMigrate = canAutoMigrate;
  }

  public String getMigrationInfo() {
    return migrationInfo;
  }

  public void setMigrationInfo(String migrationInfo) {
    this.migrationInfo = migrationInfo;
  }

  public Map<String, List<PipelineElementValidationInfo>> getValidationInfos() {
    return validationInfos;
  }

  public void setValidationInfos(Map<String, List<PipelineElementValidationInfo>> validationInfos) {
    this.validationInfos = validationInfos;
  }
}
