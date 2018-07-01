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
package org.streampipes.processors.transformation.flink.processor.converter;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.transformation.flink.config.TransformationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.XSD;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class FieldConverterController extends
        FlinkDataProcessorDeclarer<FieldConverterParameters> {

  public static final String CONVERT_PROPERTY = "convert-property";
  public static final String TARGET_TYPE = "target-type";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("field-converter", "Field Converter",
            "Converts a string value to a number data type")
            .iconUrl(TransformationFlinkConfig.getIconUrl("field_converter"))
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(), Labels.from
                            (CONVERT_PROPERTY,"Property", "The" +
                                    " property to convert"), PropertyScope.NONE)
                    .build())
            .requiredSingleValueSelection(Labels.from(TARGET_TYPE, "Datatype", "The target datatype"), Options.from
                    (new Tuple2<>("Float", XSD._float.toString()), new Tuple2<>
                            ("Integer", XSD._integer.toString())))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .outputStrategy(OutputStrategies.transform(TransformOperations
                    .dynamicDatatypeTransformation(CONVERT_PROPERTY, TARGET_TYPE)))
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<FieldConverterParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String convertProperty = extractor.mappingPropertyValue(CONVERT_PROPERTY);
    String targetDatatype =  extractor.selectedSingleValueInternalName(TARGET_TYPE, String.class);

    FieldConverterParameters staticParams = new FieldConverterParameters(
            graph,
            convertProperty,
            targetDatatype
    );

    return new FieldConverterProgram(staticParams);
  }
}
