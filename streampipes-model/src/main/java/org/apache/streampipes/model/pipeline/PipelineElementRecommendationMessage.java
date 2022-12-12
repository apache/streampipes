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

package org.apache.streampipes.model.pipeline;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class PipelineElementRecommendationMessage {

  private List<PipelineElementRecommendation> possibleElements;
  private List<PipelineElementRecommendation> recommendedElements;

  private boolean success;

  public PipelineElementRecommendationMessage() {
    this.possibleElements = new ArrayList<>();
    this.recommendedElements = new ArrayList<>();
    this.success = true;
  }

  public List<PipelineElementRecommendation> getPossibleElements() {
    return possibleElements;
  }

  public void setPossibleElements(List<PipelineElementRecommendation> possibleElements) {
    this.possibleElements = possibleElements;
  }

  public void addPossibleElement(PipelineElementRecommendation recommendation) {
    possibleElements.add(recommendation);
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public List<PipelineElementRecommendation> getRecommendedElements() {
    return recommendedElements;
  }

  public void setRecommendedElements(
      List<PipelineElementRecommendation> recommendedElements) {
    this.recommendedElements = recommendedElements;
  }


}
