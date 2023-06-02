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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.HashMap;

public class StringCounterProcessor extends StreamPipesDataProcessor {

  private static Logger log;

  protected static final String FIELD_ID = "field";
  private static final String COUNT_FIELD_ID = "countField";
  private static final String CHANGE_FROM_FIELD_ID = "changeFromField";
  private static final String CHANGE_TO_FIELD_ID = "changeToField";

  public static final String COUNT_FIELD_RUNTIME_NAME = "counter";
  public static final String CHANGE_FROM_FIELD_RUNTIME_NAME = "change_from";
  public static final String CHANGE_TO_FIELD_RUNTIME_NAME = "change_to";

  public String selectedFieldName;
  private String fieldValueOfLastEvent;

  private HashMap<String, Integer> changeCounter;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.stringoperator.counter")
        .category(DataProcessorType.STRING_OPERATOR, DataProcessorType.COUNT_OPERATOR)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.stringReq(),
                Labels.withId(FIELD_ID),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(
            EpProperties.stringEp(Labels.withId(CHANGE_FROM_FIELD_ID), CHANGE_FROM_FIELD_RUNTIME_NAME,
                "http://schema.org/String"),
            EpProperties.stringEp(Labels.withId(CHANGE_TO_FIELD_ID), CHANGE_TO_FIELD_RUNTIME_NAME,
                "http://schema.org/String"),
            EpProperties.numberEp(Labels.withId(COUNT_FIELD_ID), COUNT_FIELD_RUNTIME_NAME, "http://schema.org/Number")
        ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    log = parameters.getGraph().getLogger(StringCounterProcessor.class);
    ProcessingElementParameterExtractor extractor = parameters.extractor();
    this.selectedFieldName = extractor.mappingPropertyValue(FIELD_ID);
    this.fieldValueOfLastEvent = "";
    this.changeCounter = new HashMap<>();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String value = event.getFieldBySelector(selectedFieldName).getAsPrimitive().getAsString();
    String key = this.fieldValueOfLastEvent + ">" + value;
    boolean updateCounter = false;

    if (!this.fieldValueOfLastEvent.equals(value) && !this.fieldValueOfLastEvent.isEmpty()) {
      updateCounter = true;
      changeCounter.put(key, changeCounter.getOrDefault(key, 0) + 1);
    }

    if (updateCounter) {
      event.addField(StringCounterProcessor.CHANGE_FROM_FIELD_RUNTIME_NAME, this.fieldValueOfLastEvent);
      event.addField(StringCounterProcessor.CHANGE_TO_FIELD_RUNTIME_NAME, value);
      event.addField(StringCounterProcessor.COUNT_FIELD_RUNTIME_NAME, changeCounter.get(key));
      collector.collect(event);
    }

    this.fieldValueOfLastEvent = value;
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
