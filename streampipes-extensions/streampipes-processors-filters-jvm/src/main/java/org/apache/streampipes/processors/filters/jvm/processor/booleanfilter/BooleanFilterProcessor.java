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
package org.apache.streampipes.processors.filters.jvm.processor.booleanfilter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;


public class BooleanFilterProcessor extends StreamPipesDataProcessor {

  private static final String BOOLEAN_MAPPING = "boolean-mapping";
  public static final String VALUE = "value";

  private static final String OPTION_FALSE = "False";
  private static final String OPTION_TRUE = "True";

  private Boolean valueToFilter;
  private String mappingField;


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.processor.booleanfilter")
        .category(DataProcessorType.FILTER, DataProcessorType.BOOLEAN_OPERATOR)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredSingleValueSelection(Labels.withId(VALUE),
            Options.from(OPTION_TRUE, OPTION_FALSE))
        .outputStrategy(OutputStrategies.keep())
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.booleanReq(),
                Labels.withId(BOOLEAN_MAPPING), PropertyScope.NONE)
            .build())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
    String selectedSingleValue = processorParams.extractor().selectedSingleValue(VALUE, String.class);
    this.mappingField = processorParams.extractor().mappingPropertyValue(BOOLEAN_MAPPING);
    this.valueToFilter = selectedSingleValue.equals(OPTION_TRUE);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    if (valueToFilter == event.getFieldBySelector(this.mappingField).getAsPrimitive().getAsBoolean()) {
      spOutputCollector.collect(event);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
  }
}
