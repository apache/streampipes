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

package org.apache.streampipes.manager.matching.output;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.CustomTransformOutputStrategy;
import org.apache.streampipes.model.output.FixedOutputStrategy;
import org.apache.streampipes.model.output.KeepOutputStrategy;
import org.apache.streampipes.model.output.ListOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.output.TransformOutputStrategy;
import org.apache.streampipes.model.output.UserDefinedOutputStrategy;

public class OutputSchemaFactory {

  private OutputStrategy outputStrategy;
  private DataProcessorInvocation dataProcessorInvocation;

  public OutputSchemaFactory(DataProcessorInvocation dataProcessorInvocation) {
    this.dataProcessorInvocation = dataProcessorInvocation;
    this.outputStrategy = dataProcessorInvocation.getOutputStrategies().get(0);
  }

  public OutputSchemaGenerator<?> getOuputSchemaGenerator() {
    if (outputStrategy instanceof AppendOutputStrategy) {
      return AppendOutputSchemaGenerator.from(outputStrategy);
    } else if (outputStrategy instanceof KeepOutputStrategy) {
      return RenameOutputSchemaGenerator.from(outputStrategy);
    } else if (outputStrategy instanceof FixedOutputStrategy) {
      return FixedOutputSchemaGenerator.from(outputStrategy);
    } else if (outputStrategy instanceof CustomOutputStrategy) {
      return CustomOutputSchemaGenerator.from(outputStrategy);
    } else if (outputStrategy instanceof ListOutputStrategy) {
      return ListOutputSchemaGenerator.from(outputStrategy);
    } else if (outputStrategy instanceof TransformOutputStrategy) {
      return TransformOutputSchemaGenerator.from(outputStrategy, dataProcessorInvocation);
    } else if (outputStrategy instanceof CustomTransformOutputStrategy) {
      return CustomTransformOutputSchemaGenerator.from(outputStrategy, dataProcessorInvocation);
    } else if (outputStrategy instanceof UserDefinedOutputStrategy) {
      return UserDefinedOutputSchemaGenerator.from(outputStrategy);
    } else {
      throw new IllegalArgumentException();
    }
  }
}
