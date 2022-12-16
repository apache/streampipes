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

package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.staticproperty.MappingProperty;

import java.util.ArrayList;
import java.util.List;

public class CollectedStreamRequirements {

  private SpDataStream streamRequirements;
  private List<MappingProperty> mappingProperties;

  /**
   * Collects stream requirements and mapping properties
   *
   * @param streamRequirements All stream requirements represented as an abstract data stream.
   * @param mappingProperties  All mapping properties
   */
  public CollectedStreamRequirements(SpDataStream streamRequirements, List<MappingProperty> mappingProperties) {
    this.streamRequirements = streamRequirements;
    this.mappingProperties = mappingProperties;
  }

  public CollectedStreamRequirements() {
    this.mappingProperties = new ArrayList<>();
  }

  public SpDataStream getStreamRequirements() {
    return streamRequirements;
  }

  public void setStreamRequirements(SpDataStream streamRequirements) {
    this.streamRequirements = streamRequirements;
  }

  public List<MappingProperty> getMappingProperties() {
    return mappingProperties;
  }

  public void setMappingProperties(List<MappingProperty> mappingProperties) {
    this.mappingProperties = mappingProperties;
  }
}
