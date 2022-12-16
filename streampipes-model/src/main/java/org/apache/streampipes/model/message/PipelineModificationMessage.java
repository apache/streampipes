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

import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class PipelineModificationMessage extends Message {

  /**
   * Class that represents PipelineModification messages.
   * Modifications are used to update a pipeline element within an already created pipeline
   */

  private List<PipelineModification> pipelineModifications;
  private boolean pipelineValid;
  private List<PipelineEdgeValidation> edgeValidations;

  public PipelineModificationMessage(
      List<PipelineModification> pipelineModifications) {
    super(true);
    this.pipelineModifications = pipelineModifications;
    this.edgeValidations = new ArrayList<>();
  }

  public PipelineModificationMessage() {
    super(true);
    pipelineModifications = new ArrayList<>();
  }

  public List<PipelineModification> getPipelineModifications() {
    return pipelineModifications;
  }

  public void setPipelineModifications(
      List<PipelineModification> pipelineModifications) {
    this.pipelineModifications = pipelineModifications;
  }

  public boolean isPipelineValid() {
    return pipelineValid;
  }

  public void setPipelineValid(boolean pipelineValid) {
    this.pipelineValid = pipelineValid;
  }

  public List<PipelineEdgeValidation> getEdgeValidations() {
    return edgeValidations;
  }

  public void setEdgeValidations(List<PipelineEdgeValidation> edgeValidations) {
    this.edgeValidations = edgeValidations;
  }

  public void addPipelineModification(PipelineModification pipelineModification) {
    pipelineModifications.add(pipelineModification);
  }

  public void addEdgeValidation(PipelineEdgeValidation pipelineEdgeValidation) {
    edgeValidations.add(pipelineEdgeValidation);
  }

  public boolean existsModification(String domId) {
    for (PipelineModification modification : pipelineModifications) {
      if (modification.getDomId().contains(domId)) {
        return true;
      }
    }
    return false;
  }


}
