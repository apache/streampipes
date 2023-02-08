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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
    @JsonSubTypes.Type(SimpleTopicDefinition.class),
    @JsonSubTypes.Type(WildcardTopicDefinition.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class TopicDefinition {

  private String actualTopicName;

  public TopicDefinition() {
    super();
  }

  public TopicDefinition(String actualTopicName) {
    super();
    this.actualTopicName = actualTopicName;
  }

  public TopicDefinition(TopicDefinition other) {
    this.actualTopicName = other.getActualTopicName();
  }

  public String getActualTopicName() {
    return actualTopicName;
  }

  public void setActualTopicName(String actualTopicName) {
    this.actualTopicName = actualTopicName;
  }
}
