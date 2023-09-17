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

package org.apache.streampipes.smp.model;

import org.apache.streampipes.smp.constants.PeType;

public class AssetModel implements Comparable<AssetModel> {

  private String appId;
  private String pipelineElementName;
  private String pipelineElementDescription;
  private PeType peType = PeType.PROCESSOR;

  public AssetModel() {

  }

  public AssetModel(String appId, PeType peType) {
    this.appId = appId;
    this.peType = peType;
  }

  public AssetModel(String appId, String pipelineElementName, String pipelineElementDescription) {
    this.appId = appId;
    this.pipelineElementName = pipelineElementName;
    this.pipelineElementDescription = pipelineElementDescription;
  }

  public String getAppId() {
    return appId;
  }


  public String getPipelineElementName() {
    return pipelineElementName;
  }

  public void setPipelineElementName(String pipelineElementName) {
    this.pipelineElementName = pipelineElementName;
  }

  public String getPipelineElementDescription() {
    return pipelineElementDescription;
  }

  public void setPipelineElementDescription(String pipelineElementDescription) {
    this.pipelineElementDescription = pipelineElementDescription;
  }

  public PeType getPeType() {
    return peType;
  }

  @Override
  public String toString() {
    return pipelineElementName + " (" + peType.toString() + ", " + appId + "): "
        + pipelineElementDescription + "\n";
  }

  public int compareTo(AssetModel other) {
    return pipelineElementName.compareTo(other.getPipelineElementName());
  }
}
