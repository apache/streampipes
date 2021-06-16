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

import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

public class WildcardTopicDefinition extends TopicDefinition {

  private String wildcardTopicName;

  private List<WildcardTopicMapping> wildcardTopicMappings;

  public WildcardTopicDefinition() {
    super();
    this.wildcardTopicMappings = new ArrayList<>();
  }

  public WildcardTopicDefinition(WildcardTopicDefinition other) {
    super(other);
    this.wildcardTopicName = other.getWildcardTopicName();
    this.wildcardTopicMappings = new Cloner().wildcardTopics(other.getWildcardTopicMappings());
  }

  public WildcardTopicDefinition(String wildcardTopicName, List<WildcardTopicMapping> wildcardTopicMappings) {
    this.wildcardTopicMappings = wildcardTopicMappings;
    this.wildcardTopicName = wildcardTopicName;
  }

  public String getWildcardTopicName() {
    return wildcardTopicName;
  }

  public void setWildcardTopicName(String wildcardTopicName) {
    this.wildcardTopicName = wildcardTopicName;
  }


  public List<WildcardTopicMapping> getWildcardTopicMappings() {
    return wildcardTopicMappings;
  }

  public void setWildcardTopicMappings(List<WildcardTopicMapping> wildcardTopicMappings) {
    this.wildcardTopicMappings = wildcardTopicMappings;
  }
}
