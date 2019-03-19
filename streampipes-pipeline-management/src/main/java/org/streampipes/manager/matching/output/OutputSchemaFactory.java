/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.matching.output;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.CustomTransformOutputStrategy;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.KeepOutputStrategy;
import org.streampipes.model.output.ListOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.output.TransformOutputStrategy;

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
    } else {
      throw new IllegalArgumentException();
    }
  }
}
