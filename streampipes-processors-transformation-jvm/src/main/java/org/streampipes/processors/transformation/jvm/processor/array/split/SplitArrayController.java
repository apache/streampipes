/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.transformation.jvm.processor.array.split;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.container.api.ResolvesContainerProvidedOutputStrategy;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.ArrayList;
import java.util.List;

public class SplitArrayController extends StandaloneEventProcessingDeclarer<SplitArrayParameters>
        implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String KEEP_PROPERTIES_ID = "keep";
  public static final String ARRAY_FIELD_ID = "array-field";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.jvm.split-array")
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
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<SplitArrayParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

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
    EventProperty arrayProperty = extractor.getEventPropertyBySelector(arrayFieldSelector);
    List<EventProperty> keepProperties = extractor.getEventPropertiesBySelector
            (keepPropertySelectors);
   outProperties.add(arrayProperty);
   outProperties.addAll(keepProperties);

    return new EventSchema(outProperties);
  }
}
