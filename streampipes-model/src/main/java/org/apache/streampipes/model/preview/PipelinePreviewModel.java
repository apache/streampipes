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
package org.apache.streampipes.model.preview;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.List;

@TsModel
public class PipelinePreviewModel {

  private String previewId;

  private List<String> supportedPipelineElementDomIds;

  public PipelinePreviewModel() {
  }

  public String getPreviewId() {
    return previewId;
  }

  public void setPreviewId(String previewId) {
    this.previewId = previewId;
  }

  public List<String> getSupportedPipelineElementDomIds() {
    return supportedPipelineElementDomIds;
  }

  public void setSupportedPipelineElementDomIds(List<String> supportedPipelineElementDomIds) {
    this.supportedPipelineElementDomIds = supportedPipelineElementDomIds;
  }
}
