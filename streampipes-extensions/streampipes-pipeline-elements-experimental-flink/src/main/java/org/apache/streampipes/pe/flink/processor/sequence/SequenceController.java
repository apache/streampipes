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

package org.apache.streampipes.pe.flink.processor.sequence;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
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

public class SequenceController extends FlinkDataProcessorDeclarer<SequenceParameters> {

  private static final String TIME_WINDOW = "timeWindow";
  private static final String TIME_UNIT = "timeUnit";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.pattern-detection.flink.sequence")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataProcessorType.PATTERN_DETECT)
        .requiredStream(StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
        .requiredStream(StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
        .requiredIntegerParameter(Labels.withId(TIME_WINDOW))
        .requiredSingleValueSelection(Labels.withId(TIME_UNIT), Options.from("sec", "min",
            "hrs"))
        .outputStrategy(OutputStrategies.keep(false))
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<SequenceParameters> getProgram(DataProcessorInvocation graph,
                                                                  ProcessingElementParameterExtractor extractor) {

    Integer timeWindowSize = extractor.singleValueParameter(TIME_WINDOW, Integer.class);
    String timeUnit = extractor.selectedSingleValue(TIME_UNIT, String.class);

    SequenceParameters params = new SequenceParameters(graph, timeWindowSize, timeUnit);

    return new SequenceProgram(params);

  }
}
