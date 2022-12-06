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
package org.apache.streampipes.processors.transformation.jvm.processor.task;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

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
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm"
            + ".taskduration")
        .category(DataProcessorType.TIME)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
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
  public ConfiguredEventProcessor<TaskDurationParameters> onInvocation(DataProcessorInvocation graph,
                                                                       ProcessingElementParameterExtractor extractor) {
    String taskFieldSelector = extractor.mappingPropertyValue(TASK_FIELD_KEY);
    String timestampFieldSelector = extractor.mappingPropertyValue(TIMESTAMP_FIELD_KEY);
    String outputUnit = extractor.selectedSingleValue(OUTPUT_UNIT_ID, String.class);

    double outputDivisor = 1.0;
    if (outputUnit.equals(SECONDS)) {
      outputDivisor = 1000.0;
    } else if (outputUnit.equals(MINUTES)) {
      outputDivisor = 60000.0;
    }


    TaskDurationParameters params =
        new TaskDurationParameters(graph, taskFieldSelector, timestampFieldSelector, outputDivisor);

    return new ConfiguredEventProcessor<>(params, TaskDuration::new);
  }
}
