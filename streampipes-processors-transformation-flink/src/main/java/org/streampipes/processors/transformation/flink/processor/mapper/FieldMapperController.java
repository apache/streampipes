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
package org.streampipes.processors.transformation.flink.processor.mapper;

import org.streampipes.container.api.ResolvesContainerProvidedOutputStrategy;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;

public class FieldMapperController extends
        FlinkDataProcessorDeclarer<FieldMapperParameters> implements
        ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String REPLACE_PROPERTIES = "replaceProperties";
  private static final String FIELD_NAME = "fieldName";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.flink.field-mapper", "Field Mapper",
            "Replaces one or more field with a new field and computes a hash value of these fields")
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(), Labels.from
                            (REPLACE_PROPERTIES,"Fields", "The" +
                                    " fields to replace"), PropertyScope.NONE)
                    .build())
            .requiredTextParameter(Labels.from(FIELD_NAME, "The name of the new field ", ""))
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .outputStrategy(OutputStrategies.customTransformation())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<FieldMapperParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    List<String> replacePropertyNames = extractor.mappingPropertyValues(REPLACE_PROPERTIES);
    String newFieldName = extractor.singleValueParameter(FIELD_NAME, String.class);
    FieldMapperParameters params = new FieldMapperParameters(graph, replacePropertyNames, newFieldName);


    return new FieldMapperProgram(params);
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor extractor)  {

    List<String> replacePropertyNames = extractor.mappingPropertyValues(REPLACE_PROPERTIES);
    String newFieldName = extractor.singleValueParameter(FIELD_NAME, String.class);

    List<EventProperty> outProperties = new ArrayList<>();

    for(EventProperty prop : processingElement.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      if (replacePropertyNames.stream().anyMatch(n -> prop.getRuntimeName().equals(n))) {
        outProperties.add(prop);
      }
    }

    EventPropertyPrimitive newProperty = PrimitivePropertyBuilder.create(Datatypes.String, newFieldName).build();
    outProperties.add(newProperty);
    return new EventSchema(outProperties);
  }
}
