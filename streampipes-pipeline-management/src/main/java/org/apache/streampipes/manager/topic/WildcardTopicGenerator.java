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
package org.apache.streampipes.manager.topic;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TopicParameterType;
import org.apache.streampipes.model.grounding.WildcardTopicDefinition;
import org.apache.streampipes.model.grounding.WildcardTopicMapping;

import java.util.List;

public class WildcardTopicGenerator {

  private SpDataStream stream;

  public WildcardTopicGenerator(SpDataStream stream) {
    this.stream = stream;
  }

  public SpDataStream computeActualTopic() {
    TopicDefinition topicDefinition = stream
        .getEventGrounding()
        .getTransportProtocol()
        .getTopicDefinition();

    if (topicDefinition instanceof WildcardTopicDefinition) {
      WildcardTopicDefinition wildcardTopicDefinition = (WildcardTopicDefinition) topicDefinition;
      String actualTopicName = buildActualTopicName(wildcardTopicDefinition.getWildcardTopicName(),
          wildcardTopicDefinition
              .getWildcardTopicMappings());

      wildcardTopicDefinition.setActualTopicName(actualTopicName);
      stream.getEventGrounding().getTransportProtocol().setTopicDefinition(wildcardTopicDefinition);
    }
    return stream;
  }

  private String buildActualTopicName(String wildcardTopicName, List<WildcardTopicMapping> wildcardTopicMappings) {
    for (WildcardTopicMapping wm : wildcardTopicMappings) {
      if (TopicParameterType.valueOf(wm.getTopicParameterType()) == TopicParameterType.SENSOR_TYPE
          || TopicParameterType.valueOf(wm.getTopicParameterType()) == TopicParameterType.PLATFORM_TYPE) {
        wm.setSelectedMapping(wm.getMappingId());
      }
      wildcardTopicName = wildcardTopicName.replace("$" + wm.getMappingId(), wm.getSelectedMapping());
    }

    return wildcardTopicName;
  }
}
