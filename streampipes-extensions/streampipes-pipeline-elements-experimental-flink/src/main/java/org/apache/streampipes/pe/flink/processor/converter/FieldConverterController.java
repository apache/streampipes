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
package org.apache.streampipes.pe.flink.processor.converter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.XSD;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldConverterController extends
    FlinkDataProcessorDeclarer<FieldConverterParameters>
    implements ResolvesContainerProvidedOptions,
    ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String CONVERT_PROPERTY = "convert-property";
  public static final String TARGET_TYPE = "target-type";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.flink.field-converter")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.withId
                (CONVERT_PROPERTY), PropertyScope.NONE)
            .build())
        .requiredSingleValueSelectionFromContainer(Labels.withId(TARGET_TYPE),
            Collections.singletonList(CONVERT_PROPERTY))
        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<FieldConverterParameters> getProgram(DataProcessorInvocation graph,
                                                                        ProcessingElementParameterExtractor extractor) {
    String convertProperty = extractor.mappingPropertyValue(CONVERT_PROPERTY);
    String targetDatatype = extractor.selectedSingleValueInternalName(TARGET_TYPE, String.class);

    FieldConverterParameters staticParams = new FieldConverterParameters(
        graph,
        convertProperty,
        targetDatatype
    );

    return new FieldConverterProgram(staticParams);
  }

  @Override
  public List<Option> resolveOptions(String requestId, IStaticPropertyExtractor parameterExtractor) {
    String fieldSelector = parameterExtractor.mappingPropertyValue(CONVERT_PROPERTY);
    try {
      EventProperty property = parameterExtractor.getEventPropertyBySelector(fieldSelector);
      if (property instanceof EventPropertyPrimitive) {
        String runtimeType = ((EventPropertyPrimitive) property).getRuntimeType();
        if (runtimeType.equals(XSD.STRING.toString())) {
          return Options.from(floatValue(), integerValue(), booleanValue());
        } else if (runtimeType.equals(XSD.INTEGER.toString())) {
          return Options.from(floatValue(), stringValue(), booleanValue());
        } else if (runtimeType.equals(XSD.FLOAT.toString()) || runtimeType.equals(XSD.DOUBLE.toString())) {
          return Options.from(integerValue(), stringValue());
        } else if (runtimeType.equals(XSD.BOOLEAN.toString())) {
          return Options.from(integerValue(), stringValue());
        } else {
          return Options.from(stringValue());
        }
      } else {
        return Options.from(stringValue());
      }

    } catch (SpRuntimeException e) {
      e.printStackTrace();
      return Options.from(stringValue());
    }
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor parameterExtractor)
      throws SpRuntimeException {
    EventSchema eventSchema = processingElement.getInputStreams().get(0).getEventSchema();
    String fieldSelector = parameterExtractor.mappingPropertyValue(CONVERT_PROPERTY);
    String convertedType = parameterExtractor.selectedSingleValueInternalName(TARGET_TYPE,
        String.class);
    EventProperty property = parameterExtractor.getEventPropertyBySelector(fieldSelector);
    List<EventProperty> outputProperties = new ArrayList<>();
    if (property instanceof EventPropertyPrimitive) {
      ((EventPropertyPrimitive) property).setRuntimeType(convertedType);
    }
    eventSchema.getEventProperties().forEach(ep -> {
      if (ep.getRuntimeName().equals(property.getRuntimeName())) {
        outputProperties.add(property);
      } else {
        outputProperties.add(ep);
      }
    });

    return new EventSchema(outputProperties);
  }

  private Tuple2<String, String> floatValue() {
    return new Tuple2<>("Float", XSD.FLOAT.toString());
  }

  private Tuple2<String, String> integerValue() {
    return new Tuple2<>("Integer", XSD.INTEGER.toString());
  }

  private Tuple2<String, String> booleanValue() {
    return new Tuple2<>("Boolean", XSD.BOOLEAN.toString());
  }

  private Tuple2<String, String> stringValue() {
    return new Tuple2<>("String", XSD.STRING.toString());
  }
}
