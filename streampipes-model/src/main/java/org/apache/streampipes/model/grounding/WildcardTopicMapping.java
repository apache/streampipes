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
package org.apache.streampipes.model.grounding;

public class WildcardTopicMapping {

  private String topicParameterType;

  private String mappingId;

  private String mappedRuntimeName;

  private String selectedMapping;

  public WildcardTopicMapping(String topicParameterType, String mappingId, String mappedRuntimeName) {
    this.topicParameterType = topicParameterType;
    this.mappingId = mappingId;
    this.mappedRuntimeName = mappedRuntimeName;
  }

  public WildcardTopicMapping() {
    super();
  }

  public WildcardTopicMapping(WildcardTopicMapping other) {
    this.topicParameterType = other.getTopicParameterType();
    this.mappingId = other.getMappingId();
    this.mappedRuntimeName = other.getMappedRuntimeName();
    this.selectedMapping = other.getSelectedMapping();
  }

  public String getTopicParameterType() {
    return topicParameterType;
  }

  public void setTopicParameterType(String topicParameterType) {
    this.topicParameterType = topicParameterType;
  }

  public String getMappingId() {
    return mappingId;
  }

  public void setMappingId(String mappingId) {
    this.mappingId = mappingId;
  }

  public String getMappedRuntimeName() {
    return mappedRuntimeName;
  }

  public void setMappedRuntimeName(String mappedRuntimeName) {
    this.mappedRuntimeName = mappedRuntimeName;
  }

  public String getSelectedMapping() {
    return selectedMapping;
  }

  public void setSelectedMapping(String selectedMapping) {
    this.selectedMapping = selectedMapping;
  }
}
