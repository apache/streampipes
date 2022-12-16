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
package org.apache.streampipes.model.message;

import java.util.List;

public class PipelineEdgeValidation {

  private String sourceId;
  private String targetId;

  private EdgeValidationStatus status;

  public PipelineEdgeValidation() {
  }

  public PipelineEdgeValidation(String sourceId,
                                String targetId,
                                EdgeValidationStatus edgeValidationStatus) {
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.status = edgeValidationStatus;
  }

  public static PipelineEdgeValidation complete(String sourceId,
                                                String targetId) {
    EdgeValidationStatus status = new EdgeValidationStatus(EdgeValidationStatusType.COMPLETE);
    return new PipelineEdgeValidation(sourceId, targetId, status);
  }

  public static PipelineEdgeValidation invalid(String sourceId,
                                               String targetId,
                                               List<Notification> notifications) {
    EdgeValidationStatus status = new EdgeValidationStatus(EdgeValidationStatusType.INVALID, notifications);
    return new PipelineEdgeValidation(sourceId, targetId, status);
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getTargetId() {
    return targetId;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  public EdgeValidationStatus getStatus() {
    return status;
  }

  public void setStatus(EdgeValidationStatus status) {
    this.status = status;
  }
}
