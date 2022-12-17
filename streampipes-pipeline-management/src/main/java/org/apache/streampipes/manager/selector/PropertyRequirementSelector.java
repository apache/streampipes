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
package org.apache.streampipes.manager.selector;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;

public class PropertyRequirementSelector {

  private String requirementSelector;

  public PropertyRequirementSelector(String requirementSelector) {
    this.requirementSelector = requirementSelector;
  }

  public EventProperty findPropertyRequirement(List<SpDataStream> streamRequirements) throws
      IllegalArgumentException {
    SpDataStream affectedStream = getAffectedStream(streamRequirements);
    for (EventProperty property : affectedStream.getEventSchema().getEventProperties()) {
      if (makePropertySelector(property.getRuntimeName()).equals(requirementSelector)) {
        return property;
      }
    }

    throw new IllegalArgumentException("Could not find requirement as specified by selector.");
  }

  private String makePropertySelector(String runtimeName) {
    return getAffectedRequirementPrefix() + PropertySelectorConstants.PROPERTY_DELIMITER + runtimeName;
  }

  public SpDataStream getAffectedStream(List<SpDataStream> streams) {
    Integer affectedStreamIndex = getAffectedStreamIndex();

    if (affectedStreamIndex == 0) {
      return streams.get(0);
    } else if (affectedStreamIndex == 1 && streams.size() > 1) {
      return streams.get(1);
    } else {
      throw new IllegalArgumentException("Wrong requirement selector provided.");
    }
  }

  private Integer getAffectedStreamIndex() {
    if (requirementSelector.startsWith(PropertySelectorConstants.FIRST_REQUIREMENT_PREFIX)) {
      return 0;
    } else {
      return 1;
    }
  }

  private String getAffectedRequirementPrefix() {
    Integer affectedStreamIndex = getAffectedStreamIndex();
    if (affectedStreamIndex == 0) {
      return PropertySelectorConstants.FIRST_REQUIREMENT_PREFIX;
    } else {
      return PropertySelectorConstants.SECOND_REQUIREMENT_PREFIX;
    }
  }

  public String getAffectedStreamPrefix() {
    Integer affectedStreamIndex = getAffectedStreamIndex();
    if (affectedStreamIndex == 0) {
      return PropertySelectorConstants.FIRST_STREAM_ID_PREFIX;
    } else {
      return PropertySelectorConstants.SECOND_STREAM_ID_PREFIX;
    }
  }
}
