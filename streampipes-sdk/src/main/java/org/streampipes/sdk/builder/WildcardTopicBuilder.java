/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.sdk.builder;

import org.streampipes.model.grounding.TopicParameterType;
import org.streampipes.model.grounding.WildcardTopicDefinition;
import org.streampipes.model.grounding.WildcardTopicMapping;

public class WildcardTopicBuilder {

  private WildcardTopicDefinition wildcardTopicDefinition;

  private WildcardTopicBuilder(String wildcardTopic) {
    this.wildcardTopicDefinition = new WildcardTopicDefinition();
    this.wildcardTopicDefinition.setWildcardTopicName(wildcardTopic);

  }

  public static WildcardTopicBuilder create(String wildcardTopic) {
    return new WildcardTopicBuilder(wildcardTopic);
  }

  public WildcardTopicBuilder addMapping(WildcardTopicMapping topicMapping) {
    this.wildcardTopicDefinition.getWildcardTopicMappings().add(topicMapping);
    return this;
  }

  public WildcardTopicBuilder addSimpleMapping(TopicParameterType topicParameterType, String runtimeName) {
    this.wildcardTopicDefinition.getWildcardTopicMappings().add(new WildcardTopicMapping(topicParameterType,
            runtimeName, runtimeName));
    return this;
  }

  public WildcardTopicDefinition build() {
    return this.wildcardTopicDefinition;
  }
}
