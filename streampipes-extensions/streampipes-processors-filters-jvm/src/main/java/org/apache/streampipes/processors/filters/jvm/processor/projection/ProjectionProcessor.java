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

package org.apache.streampipes.processors.filters.jvm.processor.projection;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.List;

public class ProjectionProcessor extends StreamPipesDataProcessor {

  private List<String> outputKeys;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.project")
        .category(DataProcessorType.TRANSFORM)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .outputStrategy(OutputStrategies.custom())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
    this.outputKeys = processorParams.extractor().outputKeySelectors();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    spOutputCollector.collect(event.getSubset(outputKeys));
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
