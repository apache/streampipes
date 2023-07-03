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

package org.apache.streampipes.processors.transformation.jvm.processor.mapper;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.transformation.jvm.processor.hasher.algorithm.HashAlgorithmType;
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
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class FieldMapperProcessor extends StreamPipesDataProcessor
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String REPLACE_PROPERTIES = "replaceProperties";
  private static final String FIELD_NAME = "fieldName";

  private List<String> replacePropertyNames;
  private String newFieldName;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.field-mapper")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(), Labels.withId
                (REPLACE_PROPERTIES), PropertyScope.NONE)
            .build())
        .requiredTextParameter(Labels.withId(FIELD_NAME))
        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    this.replacePropertyNames = extractor.mappingPropertyValues(REPLACE_PROPERTIES);
    this.newFieldName = extractor.singleValueParameter(FIELD_NAME, String.class);
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    Event event = new Event();
    StringBuilder hashValue = new StringBuilder();
    List<String> keys = in.getFields().keySet().stream().sorted().collect(Collectors.toList());
    for (String key : keys) {
      if (replacePropertyNames.stream().noneMatch(r -> r.equals(key))) {
        event.addField(in.getFieldBySelector(key));
      } else {
        hashValue.append(in.getFieldBySelector((key)).getAsPrimitive().getAsString());
      }
    }

    event.addField(newFieldName, HashAlgorithmType.MD5.hashAlgorithm().toHashValue(hashValue
        .toString
            ()));
    out.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor extractor) throws SpRuntimeException {
    List<String> replacePropertyNames = extractor.mappingPropertyValues(REPLACE_PROPERTIES);
    String newFieldName = extractor.singleValueParameter(FIELD_NAME, String.class);

    List<EventProperty> outProperties = extractor.getNoneInputStreamEventPropertySubset(replacePropertyNames);

    EventPropertyPrimitive newProperty = PrimitivePropertyBuilder.create(Datatypes.String, newFieldName).build();
    outProperties.add(newProperty);
    return new EventSchema(outProperties);
  }
}
