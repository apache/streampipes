/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.processors.pattern.detection.flink.processor.sequence;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.pattern.detection.flink.config.PatternDetectionFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

public class SequenceController extends FlinkDataProcessorDeclarer<SequenceParameters> {

  private static final String TIME_WINDOW = "timeWindow";
  private static final String TIME_UNIT = "timeUnit";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("sequence", "Sequence", "Detects a sequence of events in the following form: Event A followed by Event B within X seconds. In addition, both streams can be matched by a common property value (e.g., a.machineId = b.machineId)")
            .category(DataProcessorType.PATTERN_DETECT)
            .iconUrl(PatternDetectionFlinkConfig.getIconUrl("Sequence_Icon_HQ"))
            .requiredStream(StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
            .requiredStream(StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
            .requiredIntegerParameter(TIME_WINDOW, "Time Window Size", "Size of the time window ")
            .requiredSingleValueSelection(TIME_UNIT, "Time Unit", "Specifies a unit for the time window of the " +
                    "sequence. ", Options.from("sec", "min", "hrs"))
            .outputStrategy(OutputStrategies.keep(false))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<SequenceParameters> getRuntime(DataProcessorInvocation graph,
                                                                  ProcessingElementParameterExtractor extractor) {

    Integer timeWindowSize = extractor.singleValueParameter(TIME_WINDOW, Integer.class);
    String timeUnit = extractor.selectedSingleValue(TIME_UNIT, String.class);

    SequenceParameters params = new SequenceParameters(graph, timeWindowSize, timeUnit);

    //return new SequenceProgram(params);
    return new SequenceProgram(params, new FlinkDeploymentConfig(PatternDetectionFlinkConfig.JAR_FILE,
            PatternDetectionFlinkConfig.INSTANCE.getFlinkHost(), PatternDetectionFlinkConfig.INSTANCE.getFlinkPort()));
  }
}
