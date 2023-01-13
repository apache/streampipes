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

package org.apache.streampipes.model.output;

import org.apache.streampipes.model.util.Cloner;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

@JsonSubTypes({
    @JsonSubTypes.Type(AppendOutputStrategy.class),
    @JsonSubTypes.Type(CustomOutputStrategy.class),
    @JsonSubTypes.Type(CustomTransformOutputStrategy.class),
    @JsonSubTypes.Type(FixedOutputStrategy.class),
    @JsonSubTypes.Type(KeepOutputStrategy.class),
    @JsonSubTypes.Type(ListOutputStrategy.class),
    @JsonSubTypes.Type(TransformOutputStrategy.class),
    @JsonSubTypes.Type(UserDefinedOutputStrategy.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class OutputStrategy {

  private static final long serialVersionUID = 1953204905003864143L;

  private String name;

  private List<PropertyRenameRule> renameRules;

  public OutputStrategy() {
    super();
    this.renameRules = new ArrayList<>();
  }

  public OutputStrategy(OutputStrategy other) {
    this.name = other.getName();
    this.renameRules = new Cloner().renameRules(other.getRenameRules());
  }

  public OutputStrategy(String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<PropertyRenameRule> getRenameRules() {
    return renameRules;
  }

  public void setRenameRules(List<PropertyRenameRule> renameRules) {
    this.renameRules = renameRules;
  }
}
