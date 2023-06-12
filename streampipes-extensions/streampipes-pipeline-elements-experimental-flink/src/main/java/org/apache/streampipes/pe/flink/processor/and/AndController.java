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

package org.apache.streampipes.pe.flink.processor.and;

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
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import java.util.List;

public class AndController extends FlinkDataProcessorDeclarer<AndParameters> {

  private static final String TIME_WINDOW = "time-window";
  private static final String TIME_UNIT = "time-unit";
  private static final String LEFT_MAPPING = "left-mapping";
  private static final String RIGHT_MAPPING = "right-mapping";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.pattern-detection.flink.and")
        .category(DataProcessorType.PATTERN_DETECT)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(),
                Labels.withId(LEFT_MAPPING)
                , PropertyScope.DIMENSION_PROPERTY)
            .build())
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(),
                Labels.withId(RIGHT_MAPPING)
                , PropertyScope.DIMENSION_PROPERTY)
            .build())
        .requiredSingleValueSelection(Labels.withId(TIME_UNIT), Options.from("Seconds",
            "Minutes", "Hours"))
        .requiredIntegerParameter(Labels.withId(TIME_WINDOW))
        .outputStrategy(OutputStrategies.custom(true))
        .build();
  }


  @Override
  public FlinkDataProcessorProgram<AndParameters> getProgram(DataProcessorInvocation graph,
                                                             ProcessingElementParameterExtractor extractor) {
    List<String> leftMappings = extractor.mappingPropertyValues(LEFT_MAPPING);
    List<String> rightMappings = extractor.mappingPropertyValues(RIGHT_MAPPING);
    TimeUnit timeUnit = TimeUnit.valueOf(extractor.selectedSingleValue(TIME_UNIT, String.class));
    Integer timeWindow = extractor.singleValueParameter(TIME_WINDOW, Integer.class);

    AndParameters params = new AndParameters(graph, timeUnit, timeWindow, leftMappings, rightMappings);
    return new AndProgram(params);

  }
}
