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
package org.apache.streampipes.processors.filters.jvm.processor.compose;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeProcessor extends StreamPipesDataProcessor {

  private List<String> outputKeySelectors;
  private Map<String, Event> lastEvents;
  private EventSchema outputSchema;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.compose")
        .category(DataProcessorType.TRANSFORM)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .outputStrategy(OutputStrategies.custom(true))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
    this.outputKeySelectors = processorParams.extractor().outputKeySelectors();
    this.outputSchema = processorParams.getGraph().getOutputStream().getEventSchema();
    this.lastEvents = new HashMap<>();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    this.lastEvents.put(event.getSourceInfo().getSelectorPrefix(), event);
    if (lastEvents.size() == 2) {
      spOutputCollector.collect(buildOutEvent(event.getSourceInfo().getSelectorPrefix()));
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.lastEvents.clear();
  }


  private Event buildOutEvent(String currentSelectorPrefix) {
    return EventFactory.fromEvents(lastEvents.get(currentSelectorPrefix), lastEvents.get
        (getOtherSelectorPrefix(currentSelectorPrefix)), outputSchema).getSubset(outputKeySelectors);
  }

  private String getOtherSelectorPrefix(String currentSelectorPrefix) {
    return currentSelectorPrefix.equals(PropertySelectorConstants.FIRST_STREAM_ID_PREFIX)
        ? PropertySelectorConstants.SECOND_STREAM_ID_PREFIX : PropertySelectorConstants
        .FIRST_STREAM_ID_PREFIX;
  }

}
