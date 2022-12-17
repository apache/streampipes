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

import org.apache.streampipes.manager.matching.output.OutputSchemaFactory;
import org.apache.streampipes.manager.matching.output.OutputSchemaGenerator;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComputeOutputStep extends AbstractPipelineValidationStep {

  private final Map<String, DataProcessorInvocation> relatedPes = new HashMap<>();

  @Override
  public void apply(NamedStreamPipesEntity source,
                    InvocableStreamPipesEntity target,
                    Set<InvocableStreamPipesEntity> allTargets,
                    List<PipelineElementValidationInfo> validationInfos) throws SpValidationException {

    if (target instanceof DataProcessorInvocation) {
      DataProcessorInvocation pe = (DataProcessorInvocation) target;
      Tuple2<EventSchema, ? extends OutputStrategy> outputSettings;
      OutputSchemaGenerator<?> schemaGenerator = new OutputSchemaFactory(pe)
          .getOuputSchemaGenerator();

      if (target.getInputStreams().size() == 1) {
        outputSettings = schemaGenerator.buildFromOneStream(
            pe.getInputStreams()
                .get(0));
      } else if (relatedPes.containsKey(pe.getDom())) {
        DataProcessorInvocation existingInvocation = relatedPes.get(pe.getDom());

        outputSettings = schemaGenerator.buildFromTwoStreams(existingInvocation
            .getInputStreams().get(0), pe.getInputStreams().get(1));
      } else {
        relatedPes.put(target.getDom(), pe);
        outputSettings = new Tuple2<>(new EventSchema(), pe
            .getOutputStrategies().get(0));
      }

      pe.setOutputStrategies(Collections.singletonList(outputSettings.v));
      ((DataProcessorInvocation) target).getOutputStream().setEventSchema(outputSettings.k);
    }
  }
}
