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

package org.apache.streampipes.processors.transformation.jvm.processor.state.buffer;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateBufferProcessor extends StreamPipesDataProcessor {

  public static final String TIMESTAMP_FIELD_ID = "timestampId";
  public static final String STATE_FIELD_ID = "stateId";
  public static final String SENSOR_VALUE_FIELD_ID = "sensorValueFieldId";

  public static final String VALUES = "values";
  public static final String STATE = "state";
  public static final String TIMESTAMP = "timestamp";

  private String timeProperty;
  private String stateProperty;
  private String sensorValueProperty;

  private Map<String, List> stateBuffer;


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.processor.state.buffer")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.timestampReq(),
                Labels.withId(TIMESTAMP_FIELD_ID),
                PropertyScope.HEADER_PROPERTY)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.domainPropertyReqList(SPSensor.STATE),
                Labels.withId(STATE_FIELD_ID),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.numberReq(),
                Labels.withId(SENSOR_VALUE_FIELD_ID),
                PropertyScope.MEASUREMENT_PROPERTY)
            .build()
        )
        .outputStrategy(OutputStrategies.fixed(
            EpProperties.timestampProperty(TIMESTAMP),
            EpProperties.listDoubleEp(Labels.withId(VALUES), VALUES, SO.NUMBER),
            EpProperties.listStringEp(Labels.withId(STATE), STATE, SPSensor.STATE)
        ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    timeProperty = extractor.mappingPropertyValue(TIMESTAMP_FIELD_ID);
    stateProperty = extractor.mappingPropertyValue(STATE_FIELD_ID);
    sensorValueProperty = extractor.mappingPropertyValue(SENSOR_VALUE_FIELD_ID);
    stateBuffer = new HashMap<>();
  }

  @Override
  public void onEvent(Event inputEvent,
                      SpOutputCollector collector) throws SpRuntimeException {
    long timestamp = inputEvent.getFieldBySelector(this.timeProperty).getAsPrimitive().getAsLong();
    List<String> states = inputEvent.getFieldBySelector(this.stateProperty).getAsList().parseAsSimpleType(String.class);
    double value = inputEvent.getFieldBySelector(this.sensorValueProperty).getAsPrimitive().getAsDouble();

    // add value to state buffer
    for (String state : states) {
      if (stateBuffer.containsKey(state)) {
        stateBuffer.get(state).add(value);
      } else {
        List tmp = new ArrayList();
        tmp.add(value);
        stateBuffer.put(state, tmp);
      }
    }

    // emit event if state is not in event anymore
    List<String> keysToRemove = new ArrayList<>();
    for (String key : stateBuffer.keySet()) {
      if (!states.contains(key)) {
        Event resultEvent = new Event();
        resultEvent.addField(StateBufferProcessor.VALUES, stateBuffer.get(key));
        resultEvent.addField(StateBufferProcessor.STATE, Arrays.asList(key));
        resultEvent.addField(StateBufferProcessor.TIMESTAMP, timestamp);
        collector.collect(resultEvent);
        keysToRemove.add(key);
      }
    }

    for (String s : keysToRemove) {
      stateBuffer.remove(s);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
