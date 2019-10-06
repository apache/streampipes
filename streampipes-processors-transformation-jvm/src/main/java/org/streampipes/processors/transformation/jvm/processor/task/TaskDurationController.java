/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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
package org.streampipes.processors.transformation.jvm.processor.task;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class TaskDurationController extends StandaloneEventProcessingDeclarer<TaskDurationParameters> {

  private static final String TASK_FIELD_KEY = "task-field";
  private static final String TIMESTAMP_FIELD_KEY = "timestamp-field";

  private static final String TASK_ID = "process-id";
  private static final String DURATION_ID = "duration-id";

  public static final String OUTPUT_UNIT_ID = "outputUnit";
  private static final String MILLISECONDS = "Milliseconds";
  private static final String SECONDS = "Seconds";
  private static final String MINUTES = "Minutes";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.jvm"
            + ".taskduration")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.anyProperty(),
                            Labels.withId(TASK_FIELD_KEY),
                            PropertyScope.NONE)
                    .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                            Labels.withId(TIMESTAMP_FIELD_KEY), PropertyScope.NONE)
                    .build())
            .requiredSingleValueSelection(Labels.withId(OUTPUT_UNIT_ID), Options.from(MILLISECONDS, SECONDS, MINUTES))
            .outputStrategy(OutputStrategies.fixed(EpProperties.stringEp(Labels.withId(TASK_ID),
                    "processId", "http://schema.org/taskId"),
                    EpProperties.integerEp(Labels.withId(DURATION_ID), "duration",
                            "http://schema.org/duration")))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<TaskDurationParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String taskFieldSelector = extractor.mappingPropertyValue(TASK_FIELD_KEY);
    String timestampFieldSelector = extractor.mappingPropertyValue(TIMESTAMP_FIELD_KEY);
    String outputUnit = extractor.selectedSingleValue(OUTPUT_UNIT_ID, String.class);

    double outputDivisor= 1.0;
    if (outputUnit.equals(SECONDS)) {
      outputDivisor = 1000.0;
    } else if (outputUnit.equals(MINUTES)) {
      outputDivisor = 60000.0;
    }


    TaskDurationParameters params = new TaskDurationParameters(graph, taskFieldSelector, timestampFieldSelector, outputDivisor);

    return new ConfiguredEventProcessor<>(params, TaskDuration::new);
  }
}
