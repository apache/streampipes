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

package org.apache.streampipes.processors.transformation.jvm.processor.value.change;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ChangedValueDetectionProcessor implements IStreamPipesDataProcessor {

  public static final String COMPARE_FIELD_ID = "compare";
  public static final String CHANGE_FIELD_NAME = "change_detected";
  private Integer maxStateSize = 5000; // Max number of stored states

  private String compareParameter;

  private HashMap<String, Object> dimensionsState = new HashMap<>();

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        ChangedValueDetectionProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.transformation.jvm.changed-value", 0)
            .category(DataProcessorType.VALUE_OBSERVER)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION)
            .requiredStream(StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                    Labels.withId(COMPARE_FIELD_ID),
                    PropertyScope.NONE)
                .build())
            .outputStrategy(OutputStrategies.append(EpProperties.timestampProperty(CHANGE_FIELD_NAME)))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters parameters,
                                SpOutputCollector spOutputCollector,
                                EventProcessorRuntimeContext runtimeContext) {
    compareParameter = parameters.extractor().mappingPropertyValue(COMPARE_FIELD_ID);
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector collector) {
    Object newObject = inputEvent.getFieldBySelector(compareParameter).getRawValue();

    String dimensionKey = this.getDimensionKey(inputEvent);
    var lastObject = this.dimensionsState.get(dimensionKey);

    if (lastObject == null && this.dimensionsState.size() > maxStateSize) {
      throw new SpRuntimeException("Max state size reached " + maxStateSize);
    }

    if (newObject != null) {
      if (!newObject.equals(lastObject)) {
        dimensionsState.put(dimensionKey, newObject);
        //TODO: Better handling of multiple timestamps (if the field "change_detected" is already in the input)?
        inputEvent.addField(CHANGE_FIELD_NAME, System.currentTimeMillis());
        collector.collect(inputEvent);
      }
    }
  }

  String getDimensionKey(Event inputEvent) {
    List<EventProperty> eventProperties = inputEvent.getSchemaInfo().getEventSchema().getEventProperties();

    return eventProperties.stream()
        .filter(ep ->
          ep.getPropertyScope() != null
          && ep.getPropertyScope().equals(PropertyScope.DIMENSION_PROPERTY.name())
        )
        .map(ep -> inputEvent.getFieldByRuntimeName(ep.getRuntimeName()).getRawValue().toString())
        .sorted()
        .collect(Collectors.joining());
  }

  @Override
  public void onPipelineStopped() {
  }
}
