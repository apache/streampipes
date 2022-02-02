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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

public class PipelineModification {

  private String domId;
  private String elementId;
  private boolean pipelineElementValid;

  private List<PipelineElementValidationInfo> validationInfos;
  private List<StaticProperty> staticProperties;
  private List<OutputStrategy> outputStrategies;
  private List<SpDataStream> inputStreams;
  private SpDataStream outputStream;

  public PipelineModification(String domId, String elementId,
                              List<StaticProperty> staticProperties) {
    super();
    this.domId = domId;
    this.elementId = elementId;
    this.staticProperties = staticProperties;
    this.inputStreams = new ArrayList<>();
    this.outputStrategies = new ArrayList<>();
    this.validationInfos = new ArrayList<>();
  }

  public PipelineModification() {

  }

  public String getDomId() {
    return domId;
  }

  public void setDomId(String domId) {
    this.domId = domId;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public List<OutputStrategy> getOutputStrategies() {
    return outputStrategies;
  }

  public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
    this.outputStrategies = outputStrategies;
  }

  public List<SpDataStream> getInputStreams() {
    return inputStreams;
  }

  public void setInputStreams(List<SpDataStream> inputStreams) {
    this.inputStreams = inputStreams;
  }

  public void addInputStream(SpDataStream inputStream) {
    this.inputStreams.add(inputStream);
  }

  public boolean isPipelineElementValid() {
    return pipelineElementValid;
  }

  public void setPipelineElementValid(boolean pipelineElementValid) {
    this.pipelineElementValid = pipelineElementValid;
  }

  public List<PipelineElementValidationInfo> getValidationInfos() {
    return validationInfos;
  }

  public void setValidationInfos(List<PipelineElementValidationInfo> validationInfos) {
    this.validationInfos = validationInfos;
  }

  public SpDataStream getOutputStream() {
    return outputStream;
  }

  public void setOutputStream(SpDataStream outputStream) {
    this.outputStream = outputStream;
  }
}
