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

package org.apache.streampipes.manager.matching.v2.pipeline;

import org.apache.streampipes.manager.selector.PropertySelectorGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateOutputStrategiesStep extends AbstractPipelineValidationStep {

  @Override
  public void apply(NamedStreamPipesEntity source,
                    InvocableStreamPipesEntity target,
                    Set<InvocableStreamPipesEntity> allTargets,
                    List<PipelineElementValidationInfo> validationInfos) throws SpValidationException {
    List<SpDataStream> inputStreams = target.getInputStreams();
    if (target instanceof DataProcessorInvocation) {
      ((DataProcessorInvocation) target)
          .getOutputStrategies()
          .forEach(strategy -> {
            if (strategy instanceof CustomOutputStrategy) {
              handleCustomOutputStrategy(inputStreams, (CustomOutputStrategy) strategy, validationInfos);
            }
          });
    }
  }

  private void handleCustomOutputStrategy(List<SpDataStream> inputStreams,
                                          CustomOutputStrategy strategy,
                                          List<PipelineElementValidationInfo> validationInfos) {
    PropertySelectorGenerator generator = getGenerator(inputStreams, strategy);
    strategy.setAvailablePropertyKeys(generator.generateSelectors());
    // delete selected keys that are not present as available keys
    if (invalidPropertyKeysSelected(strategy)) {
      List<String> selected = getValidSelectedPropertyKeys(strategy);
      strategy.setSelectedPropertyKeys(selected);
      validationInfos.add(PipelineElementValidationInfo.info("Auto-updated list of available fields"));
    }
  }

  private PropertySelectorGenerator getGenerator(List<SpDataStream> inputStreams,
                                                 CustomOutputStrategy strategy) {
    if (inputStreams.size() == 1 || (inputStreams.size() > 1 && !strategy.isOutputRight())) {
      return new PropertySelectorGenerator(
          inputStreams.get(0).getEventSchema(),
          false
      );
    } else {
      return new PropertySelectorGenerator(
          inputStreams.get(0).getEventSchema(),
          inputStreams.get(1).getEventSchema(),
          false
      );
    }
  }

  private boolean invalidPropertyKeysSelected(CustomOutputStrategy strategy) {
    return !strategy.getAvailablePropertyKeys().containsAll(strategy.getSelectedPropertyKeys());
  }

  private List<String> getValidSelectedPropertyKeys(CustomOutputStrategy strategy) {
    return strategy
        .getSelectedPropertyKeys()
        .stream()
        .filter(p -> strategy.getAvailablePropertyKeys().contains(p)).collect(Collectors.toList());
  }
}
