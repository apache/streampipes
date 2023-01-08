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

package org.apache.streampipes.processors.transformation.jvm.processor.array.split;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.ArrayList;
import java.util.List;

public class SplitArrayController extends StandaloneEventProcessingDeclarer<SplitArrayParameters>
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String KEEP_PROPERTIES_ID = "keep";
  public static final String ARRAY_FIELD_ID = "array-field";
  public static final String VALUE = "array_value";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.split-array")
        .category(DataProcessorType.TRANSFORM)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(),
                Labels.withId(KEEP_PROPERTIES_ID),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.listRequirement(),
                Labels.withId(ARRAY_FIELD_ID),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public ConfiguredEventProcessor<SplitArrayParameters> onInvocation(DataProcessorInvocation graph,
                                                                     ProcessingElementParameterExtractor extractor) {

    String arrayField = extractor.mappingPropertyValue(ARRAY_FIELD_ID);
    List<String> keepProperties = extractor.mappingPropertyValues(KEEP_PROPERTIES_ID);

    SplitArrayParameters params = new SplitArrayParameters(graph, arrayField, keepProperties);
    return new ConfiguredEventProcessor<>(params, SplitArray::new);
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor extractor)
      throws SpRuntimeException {
    String arrayFieldSelector = extractor.mappingPropertyValue(ARRAY_FIELD_ID);
    List<String> keepPropertySelectors = extractor.mappingPropertyValues(KEEP_PROPERTIES_ID);

    List<EventProperty> outProperties = new ArrayList<>();
    EventPropertyList arrayProperty = (EventPropertyList) extractor.getEventPropertyBySelector(arrayFieldSelector);
    EventProperty newProperty = arrayProperty.getEventProperty();
    newProperty.setRuntimeName(VALUE);
    newProperty.setLabel("Array Value");
    newProperty.setDescription("Contains values of the array. Created by Split Array processor.");

    List<EventProperty> keepProperties = extractor.getEventPropertiesBySelector
        (keepPropertySelectors);
    outProperties.add(newProperty);
    outProperties.addAll(keepProperties);

    return new EventSchema(outProperties);
  }
}
