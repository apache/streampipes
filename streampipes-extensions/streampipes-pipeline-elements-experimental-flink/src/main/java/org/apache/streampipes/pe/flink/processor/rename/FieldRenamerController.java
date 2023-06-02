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

package org.apache.streampipes.pe.flink.processor.rename;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.TransformOperations;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

public class FieldRenamerController extends FlinkDataProcessorDeclarer<FieldRenamerParameters> {

  private static final String CONVERT_PROPERTY = "convert-property";
  private static final String FIELD_NAME = "field-name";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.flink.field-renamer")
        .category(DataProcessorType.TRANSFORM)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.withId
                (CONVERT_PROPERTY), PropertyScope.NONE)
            .build())
        .requiredTextParameter(Labels.withId(FIELD_NAME))
        .outputStrategy(OutputStrategies.transform(TransformOperations
            .dynamicRuntimeNameTransformation(CONVERT_PROPERTY, FIELD_NAME)))
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<FieldRenamerParameters> getProgram(DataProcessorInvocation graph,
                                                                      ProcessingElementParameterExtractor extractor) {
    String oldPropertyName = extractor.mappingPropertyValue(CONVERT_PROPERTY);
    String newPropertyName = extractor.singleValueParameter(FIELD_NAME, String.class);

    return new FieldRenamerProgram(
        new FieldRenamerParameters(graph, oldPropertyName, newPropertyName));
  }

}
