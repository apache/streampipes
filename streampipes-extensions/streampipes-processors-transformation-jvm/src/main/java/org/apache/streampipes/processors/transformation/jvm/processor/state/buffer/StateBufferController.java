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

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
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
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class StateBufferController extends StandaloneEventProcessingDeclarer<StateBufferParameters> {

  public static final String TIMESTAMP_FIELD_ID = "timestampId";
  public static final String STATE_FIELD_ID = "stateId";
  public static final String SENSOR_VALUE_FIELD_ID = "sensorValueFieldId";

  public static final String VALUES = "values";
  public static final String STATE = "state";
  public static final String TIMESTAMP = "timestamp";


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
  public ConfiguredEventProcessor<StateBufferParameters> onInvocation(DataProcessorInvocation graph,
                                                                      ProcessingElementParameterExtractor extractor) {

    String timeProperty = extractor.mappingPropertyValue(TIMESTAMP_FIELD_ID);
    String stateProperty = extractor.mappingPropertyValue(STATE_FIELD_ID);
    String sensorValueProperty = extractor.mappingPropertyValue(SENSOR_VALUE_FIELD_ID);

    StateBufferParameters params = new StateBufferParameters(graph, timeProperty, stateProperty, sensorValueProperty);

    return new ConfiguredEventProcessor<>(params, StateBuffer::new);
  }
}
