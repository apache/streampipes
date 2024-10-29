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

package org.apache.streampipes.manager.pipeline.compact.generation;

import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.CustomTransformOutputStrategy;
import org.apache.streampipes.model.output.FixedOutputStrategy;
import org.apache.streampipes.model.output.KeepOutputStrategy;
import org.apache.streampipes.model.output.ListOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategyVisitor;
import org.apache.streampipes.model.output.TransformOutputStrategy;
import org.apache.streampipes.model.output.UserDefinedOutputStrategy;
import org.apache.streampipes.model.pipeline.compact.OutputConfiguration;
import org.apache.streampipes.model.pipeline.compact.UserDefinedOutput;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.util.List;

public class OutputStrategyGenerator implements OutputStrategyVisitor {

  private final OutputConfiguration config;

  public OutputStrategyGenerator(OutputConfiguration outputConfiguration) {
    this.config = outputConfiguration;
  }

  @Override
  public void visit(AppendOutputStrategy appendOutputStrategy) {

  }

  @Override
  public void visit(CustomOutputStrategy customOutputStrategy) {
    var keepConfig = config.keep();
    if (keepConfig != null && !keepConfig.isEmpty()) {
      customOutputStrategy.setSelectedPropertyKeys(keepConfig);
    }
  }

  @Override
  public void visit(CustomTransformOutputStrategy customTransformOutputStrategy) {

  }

  @Override
  public void visit(FixedOutputStrategy fixedOutputStrategy) {

  }

  @Override
  public void visit(KeepOutputStrategy keepOutputStrategy) {

  }

  @Override
  public void visit(ListOutputStrategy listOutputStrategy) {

  }

  @Override
  public void visit(TransformOutputStrategy transformOutputStrategy) {

  }

  @Override
  public void visit(UserDefinedOutputStrategy userDefinedOutputStrategy) {
    var userDefinedConfig = config.userDefined();
    if (userDefinedConfig != null && !userDefinedConfig.isEmpty()) {
      userDefinedOutputStrategy.setEventProperties(
          toEp(userDefinedConfig)
      );
    }
  }

  private List<EventProperty> toEp(List<UserDefinedOutput> userDefinedOutput) {
    return userDefinedOutput
        .stream()
        .map(this::toPrimitive)
        .toList();
  }

  private EventProperty toPrimitive(UserDefinedOutput u) {
    var ep = new EventPropertyPrimitive();
    ep.setRuntimeName(u.fieldName());
    ep.setSemanticType(u.semanticType());
    ep.setRuntimeType(u.runtimeType());

    return ep;
  }
}
