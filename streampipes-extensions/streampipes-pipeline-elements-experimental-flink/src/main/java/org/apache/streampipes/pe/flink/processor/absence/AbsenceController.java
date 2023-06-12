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

package org.apache.streampipes.pe.flink.processor.absence;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.pe.flink.processor.and.TimeUnit;
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

import java.util.ArrayList;
import java.util.List;

public class AbsenceController extends FlinkDataProcessorDeclarer<AbsenceParameters> {

  private static final String TIME_WINDOW = "time-window";
  private static final String TIME_UNIT = "time-unit";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.pattern-detection.flink.absence")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION)
        .category(DataProcessorType.PATTERN_DETECT)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredSingleValueSelection(Labels.withId(TIME_UNIT), Options.from("Seconds",
            "Minutes", "Hours"))
        .requiredIntegerParameter(Labels.withId(TIME_WINDOW))
        .outputStrategy(OutputStrategies.custom(false))
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<AbsenceParameters> getProgram(DataProcessorInvocation graph,
                                                                 ProcessingElementParameterExtractor extractor) {
    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : graph.getOutputStream().getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    TimeUnit timeUnit = TimeUnit.valueOf(extractor.selectedSingleValue(TIME_UNIT, String.class));
    Integer timeWindow = extractor.singleValueParameter(TIME_WINDOW, Integer.class);

    AbsenceParameters params = new AbsenceParameters(graph, selectProperties, timeWindow, timeUnit);

    return new AbsenceProgram(params);
  }
}
