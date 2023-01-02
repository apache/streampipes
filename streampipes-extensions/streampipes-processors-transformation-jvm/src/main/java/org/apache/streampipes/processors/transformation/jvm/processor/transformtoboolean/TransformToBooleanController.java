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

package org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TransformToBooleanController
    extends StandaloneEventProcessingDeclarer<TransformToBooleanParameters>
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String TRANSFORM_FIELDS_ID = "transform-fields";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.transform-to-boolean")
        .category(DataProcessorType.BOOLEAN_OPERATOR)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithNaryMapping(
                EpRequirements.anyProperty(),   // anyProperty? Would be nice, to exclude
                Labels.withId(TRANSFORM_FIELDS_ID),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public ConfiguredEventProcessor<TransformToBooleanParameters> onInvocation(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {

    List<String> transformFields = extractor.mappingPropertyValues(TRANSFORM_FIELDS_ID);

    TransformToBooleanParameters params = new TransformToBooleanParameters(graph, transformFields);

    return new ConfiguredEventProcessor<>(params, TransformToBoolean::new);
  }

  @Override
  public EventSchema resolveOutputStrategy(
      DataProcessorInvocation processingElement,
      ProcessingElementParameterExtractor parameterExtractor) throws SpRuntimeException {

    EventSchema eventSchema = new EventSchema();
    EventSchema oldEventSchema = processingElement.getInputStreams().get(0).getEventSchema();
    // Gotta remove the "s0::" in the beginning
    Set<String> transformFields =
        (parameterExtractor.mappingPropertyValues(TRANSFORM_FIELDS_ID))
            .stream()
            .map(s -> s.substring(4))
            .collect(Collectors.toSet());

    for (EventProperty eventProperty : oldEventSchema.getEventProperties()) {
      //TODO: Test, if eventProperty is a primitive type (string, number, ...)

      // if the runtimename is in transformfields, it should be converted to a boolean
      if (transformFields.contains(eventProperty.getRuntimeName())) {
        PrimitivePropertyBuilder property = PrimitivePropertyBuilder
            .create(Datatypes.Boolean, eventProperty.getRuntimeName())
            .label(eventProperty.getRuntimeName())
            .description(eventProperty.getDescription());

        eventSchema.addEventProperty(property.build());
      } else {
        // Otherwise just add the old event property
        eventSchema.addEventProperty(eventProperty);
      }
    }

    return eventSchema;
  }
}
